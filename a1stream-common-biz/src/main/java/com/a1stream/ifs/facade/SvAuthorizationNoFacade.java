package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ServiceAuthorizationVO;
import com.a1stream.ifs.bo.SvAuthorizationNoBO;
import com.a1stream.ifs.service.IfsCommService;
import com.a1stream.ifs.service.SvAuthorizationNoService;

import jakarta.annotation.Resource;

@Component
public class SvAuthorizationNoFacade {

    @Resource
    private SvAuthorizationNoService svAuthNoSer;

    @Resource
    private IfsCommService cmmSiteMstSer;

    /**
     * IX_svAuthorizationNo
     */
    public void importAuthorizationNos(List<SvAuthorizationNoBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        // 启用的经销商列表
        Set<String> dealerCds = dataList.stream().map(SvAuthorizationNoBO::getApplicationDealerCode).collect(Collectors.toSet());
        Map<String, CmmSiteMasterVO> activeDealerMap = cmmSiteMstSer.getActiveDealerMap(dealerCds);

        // Extracting dealer code as the key
        Function<SvAuthorizationNoBO, String> keyExtractor = SvAuthorizationNoBO::getApplicationDealerCode;
        Map<String, List<SvAuthorizationNoBO>> siteDataList = cmmSiteMstSer.groupBySite(dataList, keyExtractor, activeDealerMap);

        // Collection id list.
        Map<String, Set<String>> sitePointMap = new HashMap<>();
        Map<String, Set<String>> siteAuthNoMap = new HashMap<>();
        Map<String, Set<String>> siteFrameNoMap = new HashMap<>();

        Set<String> pointCds = new HashSet<>();
        Set<String> authNos = new HashSet<>();
        Set<String> frameNos = new HashSet<>();
        for (SvAuthorizationNoBO item : dataList) {
            String siteId = item.getApplicationDealerCode();
            if (activeDealerMap.keySet().contains(siteId)) {
                prepareSitePointMap(sitePointMap, item, siteId);
                prepareSiteAuthNoMap(siteAuthNoMap, item, siteId);
                prepareSiteFrameNoMap(siteFrameNoMap, item, siteId);

                pointCds.add(item.getApplicationPointCode());
                authNos.add(item.getAuthorizationNo());
                frameNos.add(item.getFrameNo());
            }
        }

        // Get FacilityInfo by [siteId + pointCode]
        Set<String> siteIdSet = activeDealerMap.keySet();
        Map<String, MstFacilityVO> facilityMap = cmmSiteMstSer.getFacilityMap(siteIdSet, pointCds);
        // Get record from ServiceAuthorizationNoList by [siteId + authorizationNo]
        Map<String, ServiceAuthorizationVO> authNoMap = svAuthNoSer.getSvAuthorizationNos(siteIdSet, authNos);

        Map<String, Long> cmmSerialProductMap = svAuthNoSer.getSerialProductMap(frameNos);

        List<ServiceAuthorizationVO> aunlist = new ArrayList<>();
        for(String siteId : activeDealerMap.keySet()) {
            // Apply inputs into ServiceAuthorizationNoList record.
            List<SvAuthorizationNoBO> items = siteDataList.get(siteId);
            for(SvAuthorizationNoBO item : items) {

                MstFacilityVO fi = facilityMap.get(ComUtil.concat(siteId, item.getApplicationPointCode()));
                Long serialProductId = cmmSerialProductMap.get(item.getFrameNo());

                ServiceAuthorizationVO sazn = authNoMap.get(ComUtil.concat(siteId, item.getAuthorizationNo()));
                if(sazn ==null) { //Create when not found.
                    sazn = new ServiceAuthorizationVO();
                }
                sazn.setPointId(fi != null? fi.getFacilityId() : null);
                sazn.setSerializedProductId(serialProductId);
                sazn.setSiteId(siteId);
                sazn.setSerializedItemNo(item.getFrameNo());
                sazn.setAuthorizationNo(item.getAuthorizationNo());
                sazn.setFromDate(item.getIssueDate());
                sazn.setToDate(item.getExpireDate());

                aunlist.add(sazn);
            }
        }

        svAuthNoSer.maintainData(aunlist);

        // TODO insert to cmm queue Info
    }

    private void prepareSitePointMap(Map<String, Set<String>> sitePointMap, SvAuthorizationNoBO item, String siteId) {
        if (sitePointMap.containsKey(siteId)) {
            sitePointMap.get(siteId).add(item.getApplicationPointCode());
        } else {
            Set<String> points = new HashSet<>();
            points.add(item.getApplicationPointCode());
            sitePointMap.put(siteId, points);
        }
    }

    private void prepareSiteAuthNoMap(Map<String, Set<String>> siteAuthNoMap, SvAuthorizationNoBO item, String siteId) {
        if (siteAuthNoMap.containsKey(siteId)) {
            siteAuthNoMap.get(siteId).add(item.getAuthorizationNo());
        } else {
            Set<String> points = new HashSet<>();
            points.add(item.getAuthorizationNo());
            siteAuthNoMap.put(siteId, points);
        }
    }

    private void prepareSiteFrameNoMap(Map<String, Set<String>> siteFrameNoMap, SvAuthorizationNoBO item, String siteId) {
        if (siteFrameNoMap.containsKey(siteId)) {
            siteFrameNoMap.get(siteId).add(item.getFrameNo());
        } else {
            Set<String> points = new HashSet<>();
            points.add(item.getFrameNo());
            siteFrameNoMap.put(siteId, points);
        }
    }
}
