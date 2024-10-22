package com.a1stream.parts.facade;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.parts.DIM020501BO;
import com.a1stream.domain.form.parts.DIM020501Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.a1stream.parts.service.DIM0205Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Work Zone Maintenance
*
* @author mid2178
*/
@Component
public class DIM0205Facade {

    @Resource
    private DIM0205Service dim0205Service;

    public List<DIM020501BO> doInitial(String siteId, Long personId) {

        return dim0205Service.getScreenData(siteId, personId);
    }

    /**
     * 删除Workzone
     */
    public void deleteWorkzone(DIM020501Form model) {

        WorkzoneVO workzoneVO = dim0205Service.findByWorkZoneId(model.getPointId(),model.getWorkzoneId());

        // 删除前验证
        validateBeforeDelete(workzoneVO,model.getSiteId());

        dim0205Service.deleteWorkzone(workzoneVO);
    }

    /**
     * 删除前验证
     */
    private void validateBeforeDelete(WorkzoneVO deleteBO, String siteId) {

        List<LocationVO> locationList = dim0205Service.getByWorkzoneId(deleteBO.getWorkzoneId(), siteId);

        // 如果数据被location引用，则无法删除
        if(!locationList.isEmpty()) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00302", new String[] {}));
        }
    }

        /**
     * 新建workzone或更新workzone
     */
    public void saveOrUpdateWorkzone(DIM020501Form model) {

        // 新增Location前验证
        this.validateNewOrModifyWorkzoneInfo(model);

        WorkzoneVO workzoneVO = this.buildWorkzoneVO(model);

        dim0205Service.saveOrUpdateWorkzone(workzoneVO);
    }

    private void validateNewOrModifyWorkzoneInfo(DIM020501Form model) {

        List<WorkzoneVO> workzoneVOs;

        // 新建时---WorkzoneCd和WorkzoneNm不允许重复，修改时---WorkzoneNm不允许重复
        if(model.getWorkzoneId() == null){
            workzoneVOs = dim0205Service.getWorkZoneByCd(model.getSiteId(), model.getWorkzoneCd(), model.getWorkzoneNm());
        }else{
            workzoneVOs = dim0205Service.getWorkzoneByNm(model.getSiteId(), model.getWorkzoneNm());
        }
        
        if(CollectionUtils.isNotEmpty(workzoneVOs)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{CodedMessageUtils.getMessage("label.workZone")}));
        }
    }

    /**
     * 创建一个WorkzoneVO对象
     */
    private WorkzoneVO buildWorkzoneVO(DIM020501Form model) {

        WorkzoneVO workzoneVO = model.getWorkzoneId()!=null ? dim0205Service.findByWorkZoneId(model.getPointId(),model.getWorkzoneId()) : new WorkzoneVO();

        workzoneVO.setSiteId(model.getSiteId());
        workzoneVO.setFacilityId(model.getPointId());
        workzoneVO.setWorkzoneCd(model.getWorkzoneCd());
        workzoneVO.setDescription(model.getWorkzoneNm());

        return workzoneVO;
    }
}
