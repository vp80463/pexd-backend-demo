package com.a1stream.common.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDisableUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminEnableUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
/**
* 功能描述:Cognito交互API
*
* mid1341
* 2024年7月10日
*/
@Slf4j
@Component
public class CognitoManager {

    @Resource
    private SystemParameterRepository systemParameterRepo;

    public void cognitoRegisterUser(String userName, String email) {

        SystemParameterVO userPool = BeanMapUtils.mapTo(systemParameterRepo.findSystemParameterBySiteIdAndSystemParameterTypeId(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.COGNITOPOOL), SystemParameterVO.class);

        AdminCreateUserRequest registerUserRequest = this.generateRegisterUserRequest(userPool.getParameterValue(), userName, email);

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();
        try {
            cognitoClient.adminCreateUser(registerUserRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessCodedException(ComUtil.t("M.E.10450"));
        }
        finally {
            cognitoClient.close();
        }
    }

    public void cognitoResetUser(String userName, String email) {
        //由于邮箱可能存在更改的情况，所以删除再新增
        SystemParameterVO userPool = BeanMapUtils.mapTo(systemParameterRepo.findSystemParameterBySiteIdAndSystemParameterTypeId(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.COGNITOPOOL), SystemParameterVO.class);

        AdminDeleteUserRequest deleteUserRequest = this.generateDeleteUserRequest(userPool.getParameterValue(), userName);
        AdminCreateUserRequest registerUserRequest = this.generateRegisterUserRequest(userPool.getParameterValue(), userName, email);

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();
        try {
            cognitoClient.adminDeleteUser(deleteUserRequest);
            cognitoClient.adminCreateUser(registerUserRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessCodedException(ComUtil.t("M.E.10450"));
        }
        finally {
            cognitoClient.close();
        }
    }

    public void cognitoDisableUser(String userName) {

        SystemParameterVO userPool = BeanMapUtils.mapTo(systemParameterRepo.findSystemParameterBySiteIdAndSystemParameterTypeId(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.COGNITOPOOL), SystemParameterVO.class);

        AdminDisableUserRequest disableUserRequest = this.generateDisableUserRequest(userPool.getParameterValue(), userName);

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();
        try {
            cognitoClient.adminDisableUser(disableUserRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessCodedException(ComUtil.t("M.E.10450"));
        }
        finally {
            cognitoClient.close();
        }
    }

    public void cognitoEnsableUser(String userName) {

        SystemParameterVO userPool = BeanMapUtils.mapTo(systemParameterRepo.findSystemParameterBySiteIdAndSystemParameterTypeId(CommonConstants.CHAR_DEFAULT_SITE_ID, SystemParameterType.COGNITOPOOL), SystemParameterVO.class);

        AdminEnableUserRequest disableUserRequest = this.generateEnableUserRequest(userPool.getParameterValue(), userName);

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();
        try {
            cognitoClient.adminEnableUser(disableUserRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessCodedException(ComUtil.t("M.E.10450"));
        }
        finally {
            cognitoClient.close();
        }
    }

    private AdminDeleteUserRequest generateDeleteUserRequest(String userPoolId, String userName) {
        return AdminDeleteUserRequest.builder()
                .userPoolId(userPoolId)
                .username(userName)
                .build();
    }

    private AdminDisableUserRequest generateDisableUserRequest(String userPoolId, String userName) {
        return AdminDisableUserRequest.builder()
                .userPoolId(userPoolId)
                .username(userName)
                .build();
    }

    private AdminEnableUserRequest generateEnableUserRequest(String userPoolId, String userName) {
        return AdminEnableUserRequest.builder()
                .userPoolId(userPoolId)
                .username(userName)
                .build();
    }

    private AdminCreateUserRequest generateRegisterUserRequest(String pool, String userName, String email) {

        List<AttributeType> userAttributes = new ArrayList<>();
        userAttributes.add(AttributeType.builder().name("email").value(email).build());
        userAttributes.add(AttributeType.builder().name("email_verified").value(CommonConstants.CHAR_TRUE).build());

        return AdminCreateUserRequest.builder()
                .userPoolId(pool)
                .username(userName)
                .userAttributes(userAttributes)
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .build();
    }
}