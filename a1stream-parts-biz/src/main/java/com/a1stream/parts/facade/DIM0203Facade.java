package com.a1stream.parts.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.DIM0203Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:re-upload Parts Data
*
* @author mid2178
*/
@Component
public class DIM0203Facade {

    @Resource
    private DIM0203Service dim0203Service;

    public void doReUploadComplete(Long facilityId, String siteId) {

       // 存在性校验
        checkExisting(facilityId, siteId);

       // 取消盘点
        SystemParameterVO sysParamUpdate = dim0203Service.getProcessingSystemParameter(siteId, facilityId, MstCodeConstants.SystemParameterType.STOCKTAKING, CommonConstants.CHAR_ONE);
        if(sysParamUpdate == null) {// 如果没在盘点中，提示信息

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }else {// update system_parameter_info

            sysParamUpdate.setParameterValue(CommonConstants.CHAR_ZERO);
        }

        // delete product_staock_tacking
        List<ProductStockTakingVO> proStoTakDel = dim0203Service.getProductStockTakingInfo(siteId, facilityId);

        dim0203Service.doCancelData(sysParamUpdate, proStoTakDel);
    }

    private void checkExisting(Long facilityId, String siteId) {

        Integer locationLine = dim0203Service.getLocationLine(facilityId, siteId);
        Integer stockLine = dim0203Service.getStockLine(facilityId, siteId);

        if(locationLine == 0 && stockLine == 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10293"));
        }else if(locationLine == 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10291"));
        }else if(stockLine == 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10292"));
        }
    }
}
