package com.a1stream.ifs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.vo.MstFacilityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SpWholeSaleSignService {

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    public List<MstFacilityVO> getMstFacilityVOList(Set<String> siteIdSet, Set<String> facilityCdSet) {

        return BeanMapUtils.mapListTo(mstFacilityRepository.findBySiteIdInAndFacilityCdIn(siteIdSet, facilityCdSet), MstFacilityVO.class);
    }

    public void doWholeSaleSign(List<MstFacilityVO> mstFacilityVOList) {

        mstFacilityRepository.saveInBatch(BeanMapUtils.mapListTo(mstFacilityVOList, MstFacility.class));
    }
}
