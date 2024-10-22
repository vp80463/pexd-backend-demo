package com.a1stream.common.manager;

import com.a1stream.common.utils.CallWebServiceUtils;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.ApplicationException;
import com.ymsl.solid.base.util.StringUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* access token缓存参数
*/
@Component
public class GetTokenManager {

    @Value("${ifs.producer.requestTokenUrl}")
    private String requestTokenUrl;
    @Value("${ifs.producer.clientId}")
    private String clientId;
    @Value("${ifs.producer.clientSecret}")
    private String clientSecret;
    @Value("${ifs.producer.grantType}")
    private String grantType;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //@Inject
    //private SysParameterRepository sysParameterRepository;

    /**
     * redis读取accessToken，如果过期则重新请求并存储到redis
     *
     * @return AccessToken
     */
    /*public String getRedisAccessToken(boolean requestFlag) {
        //请求token
        if(requestFlag){
            return requestAccessToken();
        }
    	String wmsRedisAccessToken = stringRedisTemplate.opsForValue().get("accestoken:wms:login");

    	if (wmsRedisAccessToken != null && StringUtils.isNotEmpty(wmsRedisAccessToken) && stringRedisTemplate.getExpire("accestoken:wms:login") > 0 ) {
    	    return wmsRedisAccessToken;
    	}else{
    	    return requestAccessToken();
    	}
    }*/

    /**
     * 请求获取accessToken，并且存在redis中
     *
     * @return token
     */
    /*private synchronized String requestAccessToken() {

        String token = "";

        UserAuthSysParameter tokenModel = sysParameterRepository.findFirstOneByKey(MstCodeConstants.SysParameter.WMS_INF_API_TOKEN_URL,CommonConstants.CHAR_DEFAULT_SITE_ID);
        Map<String, String> tokenMap = JsonUtils.toMap(tokenModel.getExtendList(),String.class,String.class);
        String getTokenUrl = tokenMap.get("url");
        long effectiveSeconds = NumberUtils.toLong(tokenMap.get("effectiveSeconds"));

        if (StringUtils.isNotBlankText(getTokenUrl)) {
            try {
                JSONObject resultToken = CallWebServiceUtils.getWmsTokenService(getTokenUrl,tokenMap);
                token = resultToken.getString("accessToken");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        if (token != null) {

            stringRedisTemplate.opsForValue().set("accestoken:wms:login", token.toString(), effectiveSeconds, TimeUnit.SECONDS);
        } else {

            throw new ApplicationException("access_token获取失败，错误信息：");
        }

        return token;
    }*/

    /**
     * redis读取接口平台accessToken，如果过期则重新请求并存储到redis
     * @param firstRequestFlag 第一次请求
     * @return AccessToken
     */
    public String getNewIfsAccessToken(boolean firstRequestFlag) {
        //请求token
        if(!firstRequestFlag){
            return requestNewIfsAccessToken();
        }
        String ifsRedisAccessToken = stringRedisTemplate.opsForValue().get("accestoken:newIfs:login");

        if (StringUtils.isNotBlankText(ifsRedisAccessToken)) {
            return ifsRedisAccessToken;
        }else{
            return requestNewIfsAccessToken();
        }
    }

    /**
     * 请求获取接口平台accessToken，并且存在redis中
     *
     * @return token
     */
    private synchronized String requestNewIfsAccessToken() {

        String token = "";
        Long effectiveSeconds = 0l;

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("client_id", clientId);
        userMap.put("client_secret", clientSecret);
        userMap.put("grant_type", grantType);

        if (StringUtils.isNotBlankText(requestTokenUrl)) {
            try {
                JSONObject resultToken = CallWebServiceUtils.getNewIfsTokenService(requestTokenUrl,userMap);
                token = resultToken.getString("access_token");
                effectiveSeconds = NumberUtils.toLong(resultToken.getString("expires_in"));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        if (StringUtils.isNotBlankText(token)) {

            stringRedisTemplate.opsForValue().set("accestoken:newIfs:login", token.toString(), effectiveSeconds, TimeUnit.SECONDS);
        } else {

            throw new ApplicationException("access_token获取失败，错误信息：");
        }

        return token;
    }

}
