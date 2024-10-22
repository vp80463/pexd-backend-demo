// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.a1stream.web.app.controller.test;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InitAuthDemo {

//    @Value("${aws.cognito.poolId}")
//    public String poolId;
//
//    @Value("${aws.cognito.clientId}")
//    public String clientId;
//
//    @Value("${aws.cognito.clientSecret}")
//    public String clientSecret;
//
//    @Value("${aws.cognito.authFlow}")
//    public String authFlow;
//
//    public String initiateAuth(String username, String password) {
//
//        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();
//
//        Map<String, String> authParameters = new HashMap<>();
//        authParameters.put("USERNAME", username);
//        authParameters.put("PASSWORD", password);
//
//        if (StringUtils.isNotBlank(this.clientSecret)) {
//            String secretHash = HashUtils.computeSecretHash(this.clientId, this.clientSecret, username);
//            authParameters.put("SECRET_HASH", secretHash);
//        }
//
//        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
//                                                             .authFlow(authFlow)
//                                                             .clientId(clientId)
//                                                             .authParameters(authParameters)
//                                                             .build();
//
//        try {
//            InitiateAuthResponse initiateAuthResponse = cognitoClient.initiateAuth(authRequest);
//            /*if(initiateAuthResponse.getValueForField("AuthenticationResult", AuthenticationResultType.class).isPresent()) {
//                AuthenticationResultType authenticationResultType = initiateAuthResponse.getValueForField("AuthenticationResult", AuthenticationResultType.class).get();
//                if(authenticationResultType.getValueForField("AccessToken", String.class).isPresent()){
//                    return authenticationResultType.getValueForField("AccessToken", String.class).get();
//                }
//            }else {
//                return "";
//            }*/
//            return initiateAuthResponse.getValueForField("AuthenticationResult", AuthenticationResultType.class)
//                                       .map(AuthenticationResultType::accessToken)
//                                       .orElse("");
//        } catch (CognitoIdentityProviderException e) {
//            log.error("adminInitiateAuth: ",e);
//        }finally {
//            cognitoClient.close();
//        }
//        return "";
//    }
}
