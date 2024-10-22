package com.a1stream.common.manager;

import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.common.utils.CallWebServiceUtils;
import com.ymsl.solid.base.exception.ApplicationException;
import com.ymsl.solid.base.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
* 调用新的接口平台入口
*/
@Component
@Profile(value="ifs")
public class CallNewIfsManager {

    @Resource
    private GetTokenManager getTokenManager;

    public BaseInfResponse callNewIfsService(String newIfsUrl, String interfCode,String jsonStr){

        //新ifs异步调用
        String token = getTokenManager.getNewIfsAccessToken(true);
        try {
            //获取
            BaseInfResponse respone = CallWebServiceUtils.callNewIfsServices(newIfsUrl, interfCode,token, jsonStr);
            //如果是token过期,重新请求token,再请求一次接口
            if(StringUtils.equals(respone.getCode(), "error")){
                token = getTokenManager.getNewIfsAccessToken(false);
                respone = CallWebServiceUtils.callNewIfsServices(newIfsUrl, interfCode,token, jsonStr);
            }
            return respone;
        } catch (Exception e) {
            throw new ApplicationException("调用IFS平台异常，请联系管理员！:", e);
        }
    }

    public BaseInfResponse callGetMethodNewIfsService(String newIfsUrl, String interfCode, HashMap<String, Object> params){

        //新ifs异步调用
        String token = getTokenManager.getNewIfsAccessToken(true);
        try {
            //获取
            BaseInfResponse respone = CallWebServiceUtils.callGetServices(newIfsUrl, params, interfCode, token);
            //如果是token过期,重新请求token,再请求一次接口
            if(StringUtils.equals(respone.getCode(), "error")){
                token = getTokenManager.getNewIfsAccessToken(false);
                respone = CallWebServiceUtils.callGetServices(newIfsUrl, params, interfCode, token);
            }
            return respone;
        } catch (Exception e) {
            throw new ApplicationException("调用IFS平台异常，请联系管理员！:", e);
        }
    }
}
