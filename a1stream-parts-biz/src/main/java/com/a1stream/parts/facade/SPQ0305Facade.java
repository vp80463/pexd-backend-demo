package com.a1stream.parts.facade;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ030501BO;
import com.a1stream.domain.bo.parts.SPQ030501PrintBO;
import com.a1stream.domain.bo.parts.SPQ030501PrintDetailBO;
import com.a1stream.domain.form.parts.SPQ030501Form;
import com.a1stream.parts.service.SPQ0305Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 *
* 功能描述:Parts On-Working Check List Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@Component
public class SPQ0305Facade {

    @Resource
    private SPQ0305Service spq0305Service;

    public List<SPQ030501BO> getPartsOnWorkingCheckList(SPQ030501Form form, String siteId) {

        //检索前Check
        this.check(form.getPoint(), form.getPointId(), form.getParts(), form.getPartsId());

        return spq0305Service.getPartsOnWorkingCheckList(form, siteId);
    }

    public List<SPQ030501PrintBO> getPartsOnWorkingCheckPrintList(SPQ030501Form form, String siteId) {
        List<SPQ030501PrintDetailBO> printList = spq0305Service.getPartsOnWorkingCheckPrintList(form, siteId);

        for(SPQ030501PrintDetailBO bo:printList) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
        }
        List<SPQ030501PrintBO> returnList = new ArrayList<>();
        SPQ030501PrintBO printBO = new SPQ030501PrintBO();
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        List<SPQ030501PrintDetailBO> detailList = printList;
        printBO.setDetailPrintList(detailList);
        returnList.add(printBO);
        return returnList;
    }

    private void check(String point, Long pointId, String parts, Long partsId) {

        //检查point
        if (StringUtils.isNotBlank(point) && Nulls.isNull(pointId)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             point,
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //检查parts
        if (StringUtils.isNotBlank(parts) && Nulls.isNull(partsId)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             parts,
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }
}
