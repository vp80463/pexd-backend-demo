package com.a1stream.unit.facade;

import org.springframework.stereotype.Component;

import com.a1stream.domain.vo.EntityTestTwoVO;
import com.a1stream.unit.service.AbcMIService;
import com.a1stream.unit.service.AbcService;

import jakarta.annotation.Resource;

@Component
public class AbcFacade {

    @Resource
    private AbcService abcService;

    @Resource
    private AbcMIService abcMIService;

    //service 多次更新测试
    public void repositoryCommonUpdateMutiTest() {
        abcService.repositoryCommonUpdateMutiTest();
    }

    public EntityTestTwoVO repositoryQueryTest() {

        //查询common
        EntityTestTwoVO commonData = abcService.findCommonDataByRepository();

        return null;
    }

    public void miDbTest() {

        abcMIService.findMiDBtest();
    }

    public EntityTestTwoVO repositoryCommonUpdateTest() {

        //common update
        abcService.repositoryCommonUpdateTest();

        return null;
    }

    public EntityTestTwoVO daoQueryTest() {

        //查询local
        EntityTestTwoVO localData = abcService.findLocalDataByDao();
        System.out.println("dao-----String_test:==========="+localData.getStringTest()+"======String_name:"+localData.getStringNm());

        //查询common
        EntityTestTwoVO commonData = abcService.findCommonDataByDao();
        System.out.println("dao-----String_test:==========="+commonData.getStringTest()+"======String_name:"+commonData.getStringNm());

        return null;
    }

    public EntityTestTwoVO repositoryLocalUpdateTest() {

        //local update
        abcService.repositoryLocalUpdateTest();

        return null;

    }

    public EntityTestTwoVO daoCommonUpdateTest() {

        //dao common update
        abcService.daoCommonUpdateTest();

        return null;

    }

    public EntityTestTwoVO localCommonUpdateTest() {

        //dao local update
        abcService.daoLocalUpdateTest();

        return null;
    }

    public void sendAsyncMessageToIfs() {

        abcService.sendAsyncMessageToIfs();
    }

    public void sendAsyncApiMessageToIfs() {

        abcService.sendAsyncApiMessageToIfs();
    }

    public void ifsSyncTestController() {

        abcService.sendSyncMessageToIfs();
    }


    public void ifsGetSyncTestController() {

        abcService.sendGetSyncMessageToIfs();
    }
}
