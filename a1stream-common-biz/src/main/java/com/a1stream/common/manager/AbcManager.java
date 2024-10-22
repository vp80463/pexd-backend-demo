package com.a1stream.common.manager;

import org.springframework.stereotype.Component;

import com.a1stream.domain.entity.EntityTestTwo;
import com.a1stream.domain.repository.EntityTestTwoRepository;
import com.a1stream.domain.vo.EntityTestTwoVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Component
public class AbcManager {

    @Resource
    private EntityTestTwoRepository entityTestTwoRepository;

    public EntityTestTwoVO createAbc() {

        EntityTestTwoVO entityTestTwo = new EntityTestTwoVO();

        entityTestTwo.setStringTest("common2");
        entityTestTwo.setStringNm("common_name2");

        return BeanMapUtils.mapTo(entityTestTwoRepository.save(BeanMapUtils.mapTo(entityTestTwo, EntityTestTwo.class)), EntityTestTwoVO.class);
    }

    public void modifyAbc(EntityTestTwoVO entityTestTwo) {

        entityTestTwo.setStringNm("HXC1");

        entityTestTwoRepository.save(BeanMapUtils.mapTo(entityTestTwo, EntityTestTwo.class));
    }
}
