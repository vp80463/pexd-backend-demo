package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.repository.MstBrandRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.MstBrandVO;
import com.a1stream.domain.vo.MstProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvJobMasterService {

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private MstBrandRepository mstBrandRepo;

    public void maintainData(List<MstProductVO> ps) {

        mstProductRepo.saveInBatch(BeanMapUtils.mapListTo(ps, MstProduct.class));
    }

    public MstBrandVO getYamahaBrand() {

        return BeanMapUtils.mapTo(mstBrandRepo.findByBrandCd("YAMAHA"), MstBrandVO.class);
    }

    public Map<String, MstProductVO> getMstProductMap(Set<String> productCd) {

        List<MstProductVO> resultVO = BeanMapUtils.mapListTo(mstProductRepo.findByProductCdIn(productCd), MstProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));
    }
}
