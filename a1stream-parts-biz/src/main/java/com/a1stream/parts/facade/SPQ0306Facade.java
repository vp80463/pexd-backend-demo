package com.a1stream.parts.facade;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ030601BO;
import com.a1stream.domain.bo.parts.SPQ030601PrintBO;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.form.parts.SPQ030601Form;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.SPQ0306Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stocktaking List Print
*
* mid2287
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Wang Nan      New
 */
@Component
public class SPQ0306Facade {

    @Resource
    private SPQ0306Service spq0306Service;

    public Page<SPQ030601BO> getPartsStocktakingListPageable(@RequestBody final SPQ030601Form form, String siteId) {
        this.existPoint(siteId, form.getPointId(), form.getPoint());
        return spq0306Service.getPartsStocktakingListPageable(form, siteId);
    }

    private void existPoint(String siteId, Long pointId, String point) {

        //检查point
        if (StringUtils.isNotBlank(point) && Nulls.isNull(pointId)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             point,
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        SystemParameterVO systemParameterVo = spq0306Service.findSystemParameterVOList(siteId,
                                                                                       pointId,
                                                                                       MstCodeConstants.SystemParameterType.STOCKTAKING,
                                                                                       CommonConstants.CHAR_ONE);

        if (Nulls.isNull(systemParameterVo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }
    }

    public List<SPQ030601PrintBO> getPrintPartsStocktakingResultList(SPQ030601Form form, String siteId) {
        List<SPQ030601BO> printList =spq0306Service.getPrintPartsStocktakingResultList(form, siteId);

        SystemParameter systemParameter = spq0306Service.getDisplayFlag(SystemParameterType.STOCKTAKINGDISPLAYFLAG,siteId);

        List<SPQ030601PrintBO> returnList = new ArrayList<>();
        for(SPQ030601BO bo: printList) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
        }

        SPQ030601PrintBO printBO = new SPQ030601PrintBO();
        if(systemParameter!=null && CommonConstants.CHAR_ONE.equals(systemParameter.getParameterValue())) {
            printBO.setDisplayFlag(CommonConstants.CHAR_ONE);
            printBO.setPointAbbr(printList.get(0).getPoint());
        }else {
            printBO.setDisplayFlag(CommonConstants.CHAR_ZERO);
            printBO.setPointAbbr(printList.get(0).getPoint());
        }
        printBO.setDetailPrintList(printList);
        returnList.add(printBO);

        return returnList;
    }

}