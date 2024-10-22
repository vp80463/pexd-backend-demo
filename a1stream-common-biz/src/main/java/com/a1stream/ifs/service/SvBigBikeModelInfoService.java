package com.a1stream.ifs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.entity.CmmBigBikeModel;
import com.a1stream.domain.repository.CmmBigBikeModelRepository;
import com.a1stream.domain.vo.CmmBigBikeModelVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvBigBikeModelInfoService {

    @Resource
    private CmmBigBikeModelRepository bigBikeRepo;

    public void maintainData(Set<Long> deleteIds, List<CmmBigBikeModelVO> newBigBikes) {

        //delete the all original Big Bike Model Info then add new data
        if (!deleteIds.isEmpty()) {
            bigBikeRepo.deleteOriBigBike(deleteIds);
        }

        bigBikeRepo.saveInBatch(BeanMapUtils.mapListTo(newBigBikes, CmmBigBikeModel.class));
    }

    public List<CmmBigBikeModelVO> loadOriBigBikes() {

        return BeanMapUtils.mapListTo(bigBikeRepo.findBySiteId(CommonConstants.CHAR_DEFAULT_SITE_ID), CmmBigBikeModelVO.class);
    }
}
