package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.CmmServiceDemandDetail;
import com.a1stream.domain.entity.CmmWarrantyBattery;
import com.a1stream.domain.entity.CmmWarrantySerializedProduct;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceDemandDetailRepository;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.CmmWarrantyBatteryRepository;
import com.a1stream.domain.repository.CmmWarrantySerializedProductRepository;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandDetailVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.CmmWarrantySerializedProductVO;
import com.a1stream.ifs.bo.SvWarrantyParam;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvWarrantyService {

    @Resource
    private CmmServiceDemandRepository cmmSvDemandRepo;

    @Resource
    private CmmServiceDemandDetailRepository cmmSvDemandDtlRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerialProductRepo;

    @Resource
    private CmmBatteryRepository cmmBatteryRepo;

    @Resource
    private CmmWarrantyBatteryRepository cmmWarrantyBatteryRepo;

    @Resource
    private CmmWarrantySerializedProductRepository cmmWarrantySerialProdRepo;

    public void maintainData(SvWarrantyParam param) {

        List<CmmServiceDemandDetailVO> updSvDemandDtl = param.getUpdSvDemandDtl();
        List<CmmWarrantySerializedProductVO> updWarrantySerialProd = param.getUpdWarrantySerialProd();
        List<CmmWarrantyBatteryVO> updWarrantyBattery = param.getUpdWarrantyBattery();

        cmmSvDemandDtlRepo.saveInBatch(BeanMapUtils.mapListTo(updSvDemandDtl, CmmServiceDemandDetail.class));
        cmmWarrantySerialProdRepo.saveInBatch(BeanMapUtils.mapListTo(updWarrantySerialProd, CmmWarrantySerializedProduct.class));
        cmmWarrantyBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(updWarrantyBattery, CmmWarrantyBattery.class));
    }

    public Map<Long, String> loadAllSvDemand() {

        List<CmmServiceDemandVO> resultVO = BeanMapUtils.mapListTo(cmmSvDemandRepo.findAllByOrderByBaseDateAfter(), CmmServiceDemandVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmServiceDemandVO::getServiceDemandId, CmmServiceDemandVO::getDescription, (c1, c2) -> c1));
    }

    public Map<Long, List<CmmServiceDemandDetailVO>> getSvDemandDtlMap(Set<Long> serialProdIds) {

        List<CmmServiceDemandDetail> result = cmmSvDemandDtlRepo.findBySerializedProductIdIn(serialProdIds);
        List<CmmServiceDemandDetailVO> resultVO = BeanMapUtils.mapListTo(result, CmmServiceDemandDetailVO.class);

        return resultVO.stream().collect(Collectors.groupingBy(CmmServiceDemandDetailVO::getSerializedProductId));
    }

    public Map<Long, CmmWarrantySerializedProductVO> getWarrantySerialProdMap(Set<Long> serialProdIds) {

        List<CmmWarrantySerializedProduct> result = cmmWarrantySerialProdRepo.findBySerializedProductIdIn(serialProdIds);
        List<CmmWarrantySerializedProductVO> resultVO = BeanMapUtils.mapListTo(result, CmmWarrantySerializedProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmWarrantySerializedProductVO::getSerializedProductId, Function.identity()));
    }

    public Map<String, CmmSerializedProductVO> getSerialProductMap(Set<String> frameNos) {

        List<CmmSerializedProduct> result = cmmSerialProductRepo.findByFrameNoIn(frameNos);
        List<CmmSerializedProductVO> resultVO = BeanMapUtils.mapListTo(result, CmmSerializedProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, Function.identity()));
    }

    public Map<String, CmmBatteryVO> getCmmBatteryMap(Set<String> frameNos) {

        List<CmmBattery> result = cmmBatteryRepo.findByBatteryNoIn(frameNos);
        List<CmmBatteryVO> resultVO = BeanMapUtils.mapListTo(result, CmmBatteryVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmBatteryVO::getBatteryNo, Function.identity()));
    }

    public Map<Long, CmmWarrantyBatteryVO> getWarrantyBatteryMap(Set<Long> batteryIds) {

        List<CmmWarrantyBattery> result = cmmWarrantyBatteryRepo.findByBatteryIdIn(batteryIds);
        List<CmmWarrantyBatteryVO> resultVO = BeanMapUtils.mapListTo(result, CmmWarrantyBatteryVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmWarrantyBatteryVO::getBatteryId, Function.identity()));
    }
}
