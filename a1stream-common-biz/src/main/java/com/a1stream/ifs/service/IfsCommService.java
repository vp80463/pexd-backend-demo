package com.a1stream.ifs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class IfsCommService {

    @Resource
    private CmmSiteMasterRepository cmmSiteMstRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    public Map<String, CmmSiteMasterVO> getActiveDealerMap(Set<String> siteIdSet) {

        List<CmmSiteMaster> result = cmmSiteMstRepo.findBySiteIdInAndActiveFlag(siteIdSet, CommonConstants.CHAR_Y);
        List<CmmSiteMasterVO> resultVO = BeanMapUtils.mapListTo(result, CmmSiteMasterVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSiteMasterVO::getSiteId, c -> c));
    }

    public Set<String> getAllAvaliableSiteIds() {

        List<CmmSiteMaster> result = cmmSiteMstRepo.findByActiveFlag(CommonConstants.CHAR_Y);
        List<CmmSiteMasterVO> resultVO = BeanMapUtils.mapListTo(result, CmmSiteMasterVO.class);

        return resultVO.stream().map(CmmSiteMasterVO::getSiteId).collect(Collectors.toSet());
    }

    public Map<String, MstFacilityVO> getFacilityMap(Set<String> siteIdSet, Set<String> facilityCdSet) {

        List<MstFacility> result = mstFacilityRepo.findBySiteIdInAndFacilityCdIn(siteIdSet, facilityCdSet);
        List<MstFacilityVO> resultVO = BeanMapUtils.mapListTo(result, MstFacilityVO.class);

        return resultVO.stream().collect(Collectors.toMap(item -> ComUtil.concat(item.getSiteId(), item.getFacilityCd()), item -> item));
    }

    public <T, K> Map<K, List<T>> groupBySite(List<T> dataList, Function<T, K> keyExtractor, Map<String, CmmSiteMasterVO> activeDealerMap) {

        Map<K, List<T>> siteDataMap = new HashMap<>();

        for (T item : dataList) {
            K siteId = keyExtractor.apply(item);
            if (activeDealerMap.keySet().contains(siteId.toString())) { // Convert the key to string because keys are Strings
                if (siteDataMap.containsKey(siteId)) {
                    siteDataMap.get(siteId).add(item); // Cast back to T
                } else {
                    List<T> siteData = new ArrayList<>();
                    siteData.add(item); // Cast back to T
                    siteDataMap.put(siteId, siteData);
                }
            }
        }

        return siteDataMap;
    }
}
