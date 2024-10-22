package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants.GeographyClassification;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmGeorgaphyRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ConsumerParam;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.ifs.bo.SvRegisterDocParam;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvRegisterDocService {

    @Resource
    private CmmBatteryRepository cmmBatteryRepo;

    @Resource
    private BatteryRepository batteryRepo;

    @Resource
    private CmmRegistrationDocumentRepository registDocRepo;

    @Resource
    private CmmGeorgaphyRepository geoRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerialProductRepo;

    @Resource
    private SerializedProductRepository serialProductRepo;

    @Resource
    private ConsumerManager consumerMgr;

    public void maintainData(SvRegisterDocParam param) {

        List<BatteryVO> updBatteryList = param.getUpdBatteryList();
        List<CmmBatteryVO> updCmmBatteryList = param.getUpdCmmBatteryList();

        List<CmmConsumerVO> updCmmConsumerList = param.getUpdCmmConsumerList();
        List<ConsumerPrivateDetailVO> updConsumerPrivateList = param.getUpdConsumerPrivateList();
        List<CmmRegistrationDocumentVO> updRegistDocList = param.getUpdRegistDocList();
    }

    public Map<String, CmmBatteryVO> findCmmBatteryNoMap(Set<String> batteryNos) {

        List<CmmBatteryVO> resultVO = BeanMapUtils.mapListTo(cmmBatteryRepo.findByBatteryNoIn(batteryNos), CmmBatteryVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmBatteryVO::getBatteryNo, Function.identity()));
    }

    public Map<String, BatteryVO> findBatteryNoMap(Set<String> batteryNos) {

        List<BatteryVO> resultVO = BeanMapUtils.mapListTo(batteryRepo.findByBatteryNoIn(batteryNos), BatteryVO.class);

        return resultVO.stream().collect(Collectors.toMap(BatteryVO::getBatteryNo, Function.identity()));
    }

    public Map<Long, CmmRegistrationDocumentVO> findRegistDocMap(Set<Long> batteryIds) {

        List<CmmRegistrationDocumentVO> resultVO = BeanMapUtils.mapListTo(registDocRepo.findByBatteryIdIn(batteryIds), CmmRegistrationDocumentVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmRegistrationDocumentVO::getBatteryId, Function.identity()));
    }

    public Map<String, CmmGeorgaphyVO> getCityGeoMap(Set<String> cityCds) {

        List<CmmGeorgaphyVO> resultVO = BeanMapUtils.mapListTo(geoRepo.getCityGeographys(GeographyClassification.CITY.getCodeDbid(), cityCds), CmmGeorgaphyVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmGeorgaphyVO::getGeographyCd, Function.identity()));
    }

    public Map<String, CmmSerializedProductVO> getSerialProductMap(Set<String> frameNos) {

        List<CmmSerializedProduct> result = cmmSerialProductRepo.findByFrameNoIn(frameNos);
        List<CmmSerializedProductVO> resultVO = BeanMapUtils.mapListTo(result, CmmSerializedProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, Function.identity()));
    }

    public Map<Long, SerializedProductVO> getProductMap(Set<Long> serialProductIds) {

        List<SerializedProductVO> resultVO = BeanMapUtils.mapListTo(serialProductRepo.findBySerializedProductIdIn(serialProductIds), SerializedProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(SerializedProductVO::getSerializedProductId, Function.identity()));
    }

    public Map<Long, CmmRegistrationDocumentVO> getSerialProdRegistDocMap(Set<Long> serialProductIds) {

        List<CmmRegistrationDocumentVO> resultVO = BeanMapUtils.mapListTo(registDocRepo.findBySerializedProductIdIn(serialProductIds), CmmRegistrationDocumentVO.class);

        return resultVO.stream().collect(Collectors.toMap(CmmRegistrationDocumentVO::getSerializedProductId, Function.identity()));
    }

    public ConsumerParam prepareConsumerData(BaseConsumerForm form) {
        //方法改为private，需另改写法。
        return null;//consumerMgr.prepareConsumerData(form);
    }
}
