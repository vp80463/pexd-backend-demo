package com.a1stream.master.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM021101BO;
import com.a1stream.domain.entity.BinType;
import com.a1stream.domain.form.master.CMM021101Form;
import com.a1stream.domain.repository.BinTypeRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.LocationVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class CMM0211Service {

    @Resource
    private BinTypeRepository binTypeRepository;

    @Resource
    private LocationRepository locationRepository;

    public List<CMM021101BO> findBinTypeList(String siteId) {

        return BeanMapUtils.mapListTo(binTypeRepository.findBySiteIdOrderByBinTypeCd(siteId), CMM021101BO.class);
    }

    public BinTypeVO findByBinTypeId(CMM021101Form model) {

        return BeanMapUtils.mapTo(binTypeRepository.findBySiteIdAndBinTypeId(model.getSiteId(),model.getBinTypeId()), BinTypeVO.class);
    }

    public void saveOrUpdateBinType(BinTypeVO binTypeVO) {

        binTypeRepository.save(BeanMapUtils.mapTo(binTypeVO, BinType.class));
    }

    /**
     * @param model
     * @return
     */
    public List<LocationVO> findLocationByBinTypeId(CMM021101Form model) {

        return BeanMapUtils.mapListTo(locationRepository.findBySiteIdAndBinTypeId(model.getSiteId(),model.getBinTypeId()), LocationVO.class);
    }

    /**
     * @param binType
     */
    public void deleteRow(BinType binType) {

        binTypeRepository.delete(binType);
    }
}

