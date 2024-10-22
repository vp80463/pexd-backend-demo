package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.ServiceAuthorization;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.ServiceAuthorizationRepository;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ServiceAuthorizationVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvAuthorizationNoService {

    @Resource
    private ServiceAuthorizationRepository svAuthNoRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerialProductRepo;

    public void maintainData(List<ServiceAuthorizationVO> aunlist) {

        List<ServiceAuthorization> data = BeanMapUtils.mapListTo(aunlist, ServiceAuthorization.class);

        svAuthNoRepo.saveInBatch(data);
    }

    public Map<String, ServiceAuthorizationVO> getSvAuthorizationNos(Set<String> siteIds, Set<String> authNos) {

        List<ServiceAuthorization> result = svAuthNoRepo.findBySiteIdInAndAuthorizationNoIn(siteIds, authNos);
        List<ServiceAuthorizationVO> resultVO = BeanMapUtils.mapListTo(result, ServiceAuthorizationVO.class);

        return resultVO.stream().collect(Collectors.toMap(item -> ComUtil.concat(item.getSiteId(), item.getAuthorizationNo()), item -> item));
    }

    public Map<String, Long> getSerialProductMap(Set<String> frameNos) {

        List<CmmSerializedProduct> result = cmmSerialProductRepo.findByFrameNoIn(frameNos);
        List<CmmSerializedProductVO> resultVO = BeanMapUtils.mapListTo(result, CmmSerializedProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, CmmSerializedProductVO::getSerializedProductId, (c1, c2) -> c1));
    }
}
