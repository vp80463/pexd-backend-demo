package com.a1stream.web.app.controller.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.form.master.AbcForm;
import com.a1stream.unit.facade.AbcFacade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;

@RestController
@RequestMapping("public/test")
public class AbcController implements RestProcessAware{

    @Resource
    private AbcFacade abcFacade;

    @Resource
    private InitAuthDemo initAuthDemo;

    //标准开发流程样例
    @PostMapping(value    = "/testController.json")
    public BaseResult testController(@RequestBody final AbcForm form
                                   , @AuthenticationPrincipal final PJUserDetails uc) {

        String abcParam = form.getAbcParam();

        BaseResult baseResult = new BaseResult();
        baseResult.setData("abcParam: this is test for dataSource update---------------------");

        //repository查询测试
        abcFacade.repositoryCommonUpdateTest();

        //dms manager 样例展示
        //abcFacade.repositoryCommonUpdateMutiTest();

        //更新数据
        //abcFacade.localCommonUpdateTest();

        return baseResult;
    }

    //mi数据源测试
    @PostMapping(value    = "/miDbTestController.json")
    public BaseResult miDbTestController() {

        BaseResult baseResult = new BaseResult();

        abcFacade.miDbTest();
        baseResult.setData("MI Test Success");
        return baseResult;
    }

    @PostMapping(value    = "/repositoryCommonUpdateMutiTestController.json")
    public BaseResult repositoryCommonUpdateMutiTestController() {

        BaseResult baseResult = new BaseResult();
        baseResult.setData("return test");

        abcFacade.repositoryCommonUpdateMutiTest();

        return baseResult;
    }

    //异步消息发送测试
    @PostMapping(value    = "/ifsAsyncTestController.json")
    public BaseResult ifsController() {

        BaseResult baseResult = new BaseResult();
        baseResult.setData("return test");
        //a1stream-同步消息推送设置
        //abcService.sendSyncMessageToIfs();

        //a1stream-异步消息推送设置
        abcFacade.sendAsyncMessageToIfs();

        return baseResult;
    }

    //异步消息-调用消费测试
    @PostMapping(value    = "/ifsAsyncApiTestController.json")
    public BaseResult ifsAsyncApiTestController() {

        BaseResult baseResult = new BaseResult();
        baseResult.setData("ifsAsyncApi test");
        //a1stream-同步消息推送设置
        //abcService.sendSyncMessageToIfs();

        //a1stream-异步消息推送设置
        abcFacade.sendAsyncApiMessageToIfs();

        return baseResult;
    }

    //同步消息-同步消费测试
    @PostMapping(value    = "/ifsSyncTestController.json")
    public BaseResult ifsSyncTestController() {

        BaseResult baseResult = new BaseResult();
        baseResult.setData("this is sync test");
        //a1stream-同步消息推送设置
        //abcService.sendSyncMessageToIfs();

        //a1stream-异步消息推送设置
        abcFacade.ifsSyncTestController();

        return baseResult;
    }

    //同步消息-同步消费测试-GetMethod
    @PostMapping(value    = "/ifsSyncGetTestController.json")
    public BaseResult ifsSyncGetTestController() {

        BaseResult baseResult = new BaseResult();
        baseResult.setData("this is get method sync test");
        //a1stream-同步消息推送设置
        //abcService.sendSyncMessageToIfs();

        //a1stream-异步消息推送设置
        abcFacade.ifsGetSyncTestController();

        return baseResult;
    }

    //aws SQS消息接收测试
    /*@PostMapping(value    = "/receiveMessageTestController.json")
    public BaseResult receiveMessageTestController() {

        BaseResult result = new BaseResult();

        awsSqsQueueLongPolling.receiveMessage();

        return result;
    }*/

    //aws Cognito测试
    @PostMapping(value    = "/cognitoTestController.json")
    public BaseResult cognitoTestController() {

        return new BaseResult();
    }

    //aws Cognito login test
    @PostMapping(value    = "/cognitoLoginTestController.json")
    public BaseResult cognitoLoginTestController() {

        BaseResult result = new BaseResult();

        //result.setData(initAuthDemo.initiateAuth("xxx", "xxx"));
        return result;
    }

    //aws Cognito 用户注册测试
    @PostMapping(value    = "/cognitoUserSignInTestController.json")
    public BaseResult cognitoUserSignInTestController() {

        BaseResult result = new BaseResult();

        //awsSqsQueueLongPolling.receiveMessage();

        String userPoolId = "ap-southeast-1_QKNn98WYz";
        String userName = "HXCTEST";

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();
        createNewUser(cognitoClient, userPoolId, userName);
//        signUp(cognitoClient, userPoolId, userName);


        cognitoClient.close();

        return result;
    }

    //aws Cognito 更新用户密码
    @PostMapping(value    = "/cognitoUpdateUserPasswordTestController.json")
    public BaseResult cognitoUpdateUserPasswordTestController() {

        BaseResult result = new BaseResult();

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();

        String userPoolId = "ap-southeast-1_QKNn98WYz";
        String userName = "HXCTEST";

        deleteUser(cognitoClient, userPoolId, userName);
        createNewUser(cognitoClient, userPoolId, userName);
        cognitoClient.close();

        return result;
    }



    private void deleteUser(CognitoIdentityProviderClient cognitoClient, String userPoolId, String userName) {
        try {
            AdminDeleteUserRequest adminDeleteUserRequest = AdminDeleteUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(userName)
                    .build();

            AdminDeleteUserResponse response = cognitoClient.adminDeleteUser(adminDeleteUserRequest);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void createNewUser(CognitoIdentityProviderClient cognitoClient, String userPoolId, String userName) {

        try {

            List<AttributeType> userAttributes = new ArrayList<>();
            userAttributes.add(AttributeType.builder().name("email").value("670463650@qq.com").build());
            userAttributes.add(AttributeType.builder().name("email_verified").value("true").build());

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                                                                       .userPoolId(userPoolId)
                                                                       .username(userName)
                                                                       .userAttributes(userAttributes)
                                                                       .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                                                                       .build();

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}