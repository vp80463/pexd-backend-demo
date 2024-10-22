package com.a1stream.unit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.logic.AbcLogic;
import com.a1stream.common.manager.AbcManager;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.domain.entity.EntityTestTwo;
import com.a1stream.domain.repository.EntityTestTwoRepository;
import com.a1stream.domain.vo.EntityTestTwoVO;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class AbcService {

    @Resource
    private EntityTestTwoRepository entityTestTwoRepository;

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Resource
    private AbcManager abcManager;

    @Resource
    private AbcLogic abcLogic;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    /*public void sendSyncMessageToIfs() {
        InterfReceiveModel ifsModel = new InterfReceiveModel();
        ifsModel.setInterfCode("userInfoImport");
        BaseForm baseForm = new BaseForm();
        baseForm.setUserName("testUserName");
        baseForm.setToSiteId(CommonConstants.CHAR_YMSM);
        List<BaseForm> salesOrderInfoList = new ArrayList<BaseForm>();
        salesOrderInfoList.add(baseForm);
        ifsModel.setBody(salesOrderInfoList);
        BaseInfResponse response = ifsMsgEntranceLogic.sendMessage(ifsModel);
        if (!StringUtils.equals(response.getCode(), InterfBaseResponse.ReturnValue.SUCCESS)) {

            throw new PJCustomException(response.getMessage());
        }
        System.out.println("Message Send Success...");
    }*/

    public void sendAsyncMessageToIfs() {
        String ifsCode = InterfCode.A1STREAM_YMVNDMS_ASYNCSQSTEST;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("hahah");
        String jsonStr = JSON.toJSON(arrayList).toString();
        callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
        System.out.println("SQS Async Message Send Success...");
    }

    public void sendAsyncApiMessageToIfs() {
        String ifsCode = InterfCode.A1STREAM_YMVNDMS_ASYNCAPITEST;
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("hahah async api test");
        String jsonStr = JSON.toJSON(arrayList).toString();
        callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
        System.out.println("hahah async api test...");
    }

    public void sendSyncMessageToIfs() {

        String ifsCode = InterfCode.A1STREAM_YMVNDMS_SYNCTEST;
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("hahah async api test");
        String jsonStr = JSON.toJSON(arrayList).toString();
        BaseInfResponse callNewIfsService = callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
        System.out.println("this is a sync test...");
    }

    public void sendGetSyncMessageToIfs() {

        String ifsCode = InterfCode.A1STREAM_YMVNDMS_SYNCGETTEST;
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("hahah async api test");
        HashMap<String, Object> paramsMap = new HashMap<String,Object>();
        paramsMap.put("paramTest", "paramTestHaha");
        BaseInfResponse callNewIfsService = callNewIfsManager.callGetMethodNewIfsService(ifsRequestUrl, ifsCode, paramsMap);
        System.out.println("this is a sync test...");
    }

    //不用注解时查询common库
    public EntityTestTwoVO findLocalDataByRepository() {

        EntityTestTwo localData = entityTestTwoRepository.findByStringTest("local1");
        return BeanMapUtils.mapTo(localData, EntityTestTwoVO.class);
    }

    public EntityTestTwoVO findCommonDataByRepository() {

        EntityTestTwo commonData = entityTestTwoRepository.findByStringTest("common");
        return BeanMapUtils.mapTo(commonData, EntityTestTwoVO.class);
    }

    public EntityTestTwoVO findLocalDataByDao() {

        return entityTestTwoRepository.findEntityTestTwoByStringTest("local1");
    }

    public EntityTestTwoVO findCommonDataByDao() {

        return entityTestTwoRepository.findEntityTestTwoByStringTest("common");
    }

    public void repositoryCommonUpdateMutiTest() {

        abcLogic.abcLogicTest();

        //调用manager返回结果只能以VO或者BO的形式
        EntityTestTwoVO b = abcManager.createAbc();

        //long error = 1/0;

        abcManager.modifyAbc(b);

//        abcLogic.modifyAbc(new Long("608516796907521"));
    }

    public void repositoryCommonUpdateTest() {

        //查
        Optional<EntityTestTwo> testEntityInfo = entityTestTwoRepository.findById(Long.valueOf(22222));
        if(testEntityInfo.isPresent()) {
            EntityTestTwo entityTestTwo = testEntityInfo.get();
            System.out.println("find result----------------------------: "+entityTestTwo.getStringTest()+"  "+entityTestTwo.getStringNm());
        }

        //增
        EntityTestTwo entityTestTwo = new EntityTestTwo();
        entityTestTwo.setStringTest("common2");
        entityTestTwo.setStringNm("common_name2");
        entityTestTwoRepository.save(entityTestTwo);
        System.out.println("repository-----common-----insert------success");

        //删
        EntityTestTwo common3 = entityTestTwoRepository.findByStringTest("common3");
        entityTestTwoRepository.delete(common3);
        System.out.println("repository-----common-----delete------success");

        //事务回滚测试
        //Long test = (long)1/0;

        //改
        EntityTestTwo common1 = entityTestTwoRepository.findByStringTest("common");
        common1.setStringNm("common_name1");
        entityTestTwoRepository.save(common1);
        System.out.println("repository-----common-----update------success");

    }

    public void repositoryLocalUpdateTest() {

        //增
        EntityTestTwo entityTestTwo = new EntityTestTwo();
        entityTestTwo.setStringTest("local2");
        entityTestTwo.setStringNm("local_name2");
        entityTestTwoRepository.save(entityTestTwo);
        System.out.println("repository-----local-----insert------success");

        //删
        EntityTestTwo local3 = entityTestTwoRepository.findByStringTest("local1_3");
        entityTestTwoRepository.delete(local3);
        System.out.println("repository-----local-----delete------success");

        //事务回滚测试
        //Long test = (long)1/0;

        //改
        EntityTestTwo local1 = entityTestTwoRepository.findByStringTest("local1_1");
        local1.setStringTest("local1");
        entityTestTwoRepository.save(local1);
        System.out.println("repository-----local-----update------success");
    }

    public void daoCommonUpdateTest() {

        //增
        entityTestTwoRepository.insertCommonData(Long.valueOf(202402220004L), "common4", "common_name4");
        System.out.println("dao-----common-----insert------success");

        //删
        entityTestTwoRepository.deleteCommonDataByStringTest("common5");
        System.out.println("dao-----common-----delete------success");

        //事务回滚测试
        //Long test = (long)1/0;

        //改
        entityTestTwoRepository.updateCommonDataByStringTest("common6","common_name_6");
        System.out.println("dao-----common-----update------success");
    }

    public void daoLocalUpdateTest() {

        //增
        entityTestTwoRepository.insertCommonData(Long.valueOf(202402220004L), "local1_4", "local1_name4");
        System.out.println("dao-----local-----insert------success");

        //删
        entityTestTwoRepository.deleteCommonDataByStringTest("local1_5");
        System.out.println("dao-----local-----delete------success");

        //事务回滚测试
        //Long test = (long)1/0;

        //改
        entityTestTwoRepository.updateCommonDataByStringTest("local1_6","ooo");
        System.out.println("dao-----local-----update------success");

        //update by entity
        EntityTestTwo updateTest = entityTestTwoRepository.findByStringTest("common2");
        updateTest.setStringNm("lalal");
        entityTestTwoRepository.save(updateTest);
    }
}
