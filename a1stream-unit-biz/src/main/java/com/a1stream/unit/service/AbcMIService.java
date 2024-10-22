package com.a1stream.unit.service;

import org.springframework.stereotype.Service;

import com.a1stream.domain_mi.entity.EntityTestThree;
import com.a1stream.domain_mi.repository.EntityTestThreeRepository;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

@Service
public class AbcMIService{

    @Resource
    private EntityTestThreeRepository entityTestThreeRepository;

    @MultiTenant("a1stream-mi-db")
    public void findMiDBtest() {

        //查
        EntityTestThree entityTestThree = entityTestThreeRepository.findByStringTest("common1");
        System.out.println("entityTestThree string_nmxxxx: "+entityTestThree.getStringNm());

        //增
        //EntityTestThree entityTestThree2 = new EntityTestThree();
        //entityTestThree2.setStringTest("common5");
        //entityTestThree2.setStringNm("common_name5");
        //entityTestThreeRepository.save(entityTestThree2);
        //System.out.println("repository-----common-----insert------success");

        //删
        //EntityTestThree common3 = entityTestThreeRepository.findByStringTest("common3");
        //entityTestThreeRepository.delete(common3);
        //System.out.println("repository-----common-----delete------success");

        //事务回滚测试
        //Long test = (long)1/0;

        //改
        //EntityTestThree common1 = entityTestThreeRepository.findByStringTest("common4");
        //common1.setStringNm("common_name4");
        //entityTestThreeRepository.save(common1);
        //System.out.println("repository-----common-----update------success");
    }

}
