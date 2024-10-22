package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.MCSalesType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDM030101BO;
import com.a1stream.domain.bo.unit.SDM030103BO;
import com.a1stream.domain.bo.unit.SDM030103DetailBO;
import com.a1stream.domain.form.unit.SDM030101Form;
import com.a1stream.unit.service.SDM0301Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述:Sales Order List sdm0301_01明细画面
*
* mid2330
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Liu Chaoran     New
*/
@Component
public class SDM0301Facade {

    @Resource
    private SDM0301Service sdm0301Ser;

    @Resource
    private HelperFacade helperFac;

    public List<SDM030101BO> listSalesOrderData(SDM030101Form form, PJUserDetails uc, boolean isExport) {
        //查询校验
        validateRetrieveOrder(form);
        List<SDM030101BO> salesOrderList = sdm0301Ser.findSalesOrderData(form, uc.getDealerCode());

        //对查询结果sales order S037编码转code
        Map<String, String> codeMap = helperFac.getMstCodeInfoMap(MCSalesType.CODE_ID);
        for (SDM030101BO bo : salesOrderList) {
            bo.setSalesType(codeMap.get(bo.getSalesTypeDbid()));
            if (isExport) {
                bo.setOrderDate(ComUtil.changeFormat(bo.getOrderDate()));
            }
        }
        return salesOrderList;
    }

    //查询SDM030103条件部内容
    public SDM030103BO findDealerWholeSOInfo(Long orderId) {

        SDM030103BO basicInfo = sdm0301Ser.getDealerWholeSOBasicInfo(orderId);
        List<SDM030103DetailBO> detailList = sdm0301Ser.getDealerWholeSODetails(orderId);

        if (basicInfo != null) {
            basicInfo.setDetailList(detailList);
        }

        return basicInfo;
    }

    public List<SDM030103BO> printDealerWholeSOInfo(Long orderId) {

        List<SDM030103BO> returnList = new ArrayList<>();

        SDM030103BO basicInfo = sdm0301Ser.getDealerWholeSOBasicInfo(orderId);
        List<SDM030103DetailBO> detailList = sdm0301Ser.getDealerWholeSODetails(orderId);

        if (basicInfo != null) {
            basicInfo.setDate(ComUtil.nowDateTime());
            basicInfo.setDetailList(detailList);

            returnList.add(basicInfo);
        }

        return returnList;
    }

    private void validateRetrieveOrder(SDM030101Form form) {

        //salesMonth校验，是否大于系统月份
        if(form.getSalesMonth().compareTo(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YM))) > 0) {
            throw new BusinessCodedException(ComUtil.t("error.dateEqAfter", new String[] {
                    ComUtil.t("label.sysDate"),
                    ComUtil.t("label.modelYear")}));
        }
        //dealer校验
        if (StringUtils.isNotBlank(form.getDealer()) && Nulls.isNull(form.getDealerId())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {
                                             ComUtil.t("label.dealer"),
                                             form.getDealer(),
                                             ComUtil.t("label.tableOrganizationInfo")}));
        }
    }
}
