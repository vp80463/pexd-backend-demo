package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.domain.vo.CmmBigBikeModelVO;
import com.a1stream.ifs.bo.SvBigBikeBO;
import com.a1stream.ifs.bo.SvCoupCtgBO;
import com.a1stream.ifs.bo.SvCoupCtgLevelBO;
import com.a1stream.ifs.service.SvBigBikeModelInfoService;

import jakarta.annotation.Resource;

@Component
public class SvBigBikeModelInfoFacade {

    @Resource
    private SvBigBikeModelInfoService svBigBikeSer;

    /**
     * IX_svBigBikeModelInfo
     * SvESBServiceManagerImpl importBigBikeModelInfo
     */
    public void importBigBikeModels(List<SvBigBikeBO> dataList) {

        //delete the all original Big Bike Model Info then add new data
        List<CmmBigBikeModelVO> newBigBikes = new ArrayList<>();
        List<CmmBigBikeModelVO> oriBigBikes = svBigBikeSer.loadOriBigBikes();

        for(SvBigBikeBO item : dataList) {
            String coupCtgCd = item.getCoupCtgCode();
            String plCd = item.getPlCd();
            for (SvCoupCtgLevelBO level : item.getCoupCtgLevels()) {
                String userMonthFrom = level.getUsedMonthFrom();
                String userMonthTo = level.getUsedMonthTo();
                String mileageFrom = level.getMileageFrom();
                String mileageTo = level.getMileageTo();
                Long coupCtgLevel = Long.valueOf(level.getCoupCtgLevel());
                for (SvCoupCtgBO ctg : item.getCoupCtgModels()) {

                    CmmBigBikeModelVO bigBike = new CmmBigBikeModelVO();

                    bigBike.setCoupCtgCd(coupCtgCd);
                    bigBike.setPlCd(plCd);
                    bigBike.setCoupCtgLevel(coupCtgLevel);
                    bigBike.setUserMonthFrom(userMonthFrom);
                    bigBike.setUserMonthTo(userMonthTo);
                    bigBike.setMileageFrom(mileageFrom);
                    bigBike.setMileageTo(mileageTo);
                    bigBike.setModelCd(ctg.getModelCode());

                    newBigBikes.add(bigBike);
                }
            }
        }

        Set<Long> deleteIds = oriBigBikes.stream().map(CmmBigBikeModelVO::getBigBikeModelId).collect(Collectors.toSet());
        svBigBikeSer.maintainData(deleteIds, newBigBikes);
    }
}
