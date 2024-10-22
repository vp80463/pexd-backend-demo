package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ030701BO;
import com.a1stream.domain.bo.parts.SPQ030701PrintBO;
import com.a1stream.domain.form.parts.SPQ030701Form;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.SPQ0307Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stocktaking Progress Inquiry
*
* mid2287
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Wang Nan      New
 */
@Component
public class SPQ0307Facade {

    @Resource
    private SPQ0307Service spq0307Service;

    public Page<SPQ030701BO> getPartsStocktakingProgressList(SPQ030701Form form) {
        this.check(form);
        return spq0307Service.getPartsStocktakingProgressList(form, form.getSiteId());
    }

    private void check(SPQ030701Form form) {

        //检查point
        if (StringUtils.isNotBlank(form.getPoint()) && Nulls.isNull(form.getPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             form.getPoint(),
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //检查parts
        if (StringUtils.isNotBlank(form.getParts()) && Nulls.isNull(form.getPartsId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             form.getParts(),
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }

        SystemParameterVO systemParameterVo = spq0307Service.findSystemParameterVOList(form.getSiteId(),
                                                                                       form.getPointId(),
                                                                                       MstCodeConstants.SystemParameterType.STOCKTAKING,
                                                                                       CommonConstants.CHAR_ONE);

        if (Nulls.isNull(systemParameterVo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }
    }

    public List<SPQ030701PrintBO> getPrintPartsStocktakingProgressList(SPQ030701Form form, String siteId) {
        List<SPQ030701BO> printList = spq0307Service.getPrintPartsStocktakingProgressList(form, siteId);

        for(SPQ030701BO bo:printList) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
            bo.setWz(bo.getWz());
            bo.setLocation(bo.getLocation());
            bo.setPartsNo(bo.getPartsNo());
            bo.setPartsNm(bo.getPartsNm());
            bo.setSystemQty(bo.getSystemQty() != null ? new BigDecimal(bo.getSystemQty().intValue()) : BigDecimal.ZERO);
            bo.setActualQty(bo.getActualQty() != null ? new BigDecimal(bo.getActualQty().intValue()) : BigDecimal.ZERO);
        }
        List<SPQ030701PrintBO> returnList = new ArrayList<>();
        SPQ030701PrintBO printBO = new SPQ030701PrintBO();
        if(printList != null) {
            printBO.setPointAbbr(printList.get(0).getPoint());
        }
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        List<SPQ030701BO> detailPrintList = printList;
        printBO.setDetailPrintList(detailPrintList);
        returnList.add(printBO);
        return returnList;
    }

}
