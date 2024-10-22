package com.a1stream.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseInfResponse;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.ApplicationException;

import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class CallWebServiceUtils {

    /**
     * 默认超时时间1分钟
     */
    public static final int TIME_OUT = 60000;

    public static BaseInfResponse callNewIfsServices(String apiUrl, String interfCode,String accessToken, String json)  throws Exception{

        BaseInfResponse responseModel = new BaseInfResponse();

        // 发送请求
        HttpResponse<JsonNode> response = withTimeout(Unirest.post(apiUrl), 300000)
                                                     .header("Content-Type", "application/json")
                                                     .header("interfcode", interfCode)
                                                     .header("Authorization", "Bearer " + accessToken)
                                                     .body(json).asJson();

        try {
            //如果token过期
            if(response.getBody().getObject().has("error")){
                responseModel.setCode("error");
                responseModel.setMessage(response.getBody().getObject().getString("error"));
            }else{
                // 请求结果
                String code = response.getBody().getObject().getString("code");
                String message = response.getBody().getObject().getString("message");
                if(response.getBody().getObject().get("data") != null){
                    String data = response.getBody().getObject().get("data").toString();
                    responseModel.setData(data);
                }
                responseModel.setCode(code);
                responseModel.setMessage(message);
            }

            return responseModel;
        } catch (Exception e) {
            throw new ApplicationException(response.getBody().toString());
        }

    }

    public static BaseInfResponse callIfsServices(String apiUrl, String json)  throws Exception{

        BaseInfResponse responseModel = new BaseInfResponse();

      // 发送请求
      HttpResponse<JsonNode> response = withTimeout(Unirest.post(apiUrl), 300000)
                                                 .header("Content-Type", "application/json")
                                                 .body(json).asJson();

      // 请求结果
      String code = response.getBody().getObject().getString("code");
      String message = response.getBody().getObject().getString("message");
      if(response.getBody().getObject().get("data") != null){
          String data = response.getBody().getObject().get("data").toString();
          responseModel.setData(data);
      }

      responseModel.setCode(code);
      responseModel.setMessage(message);

      return responseModel;
    }

    public static kong.unirest.json.JSONObject callPostRequest(String apiUrl, String json)  throws Exception{

        // 发送请求
        HttpResponse<JsonNode> response = withTimeout(Unirest.post(apiUrl), 300000)
                                                   .header("Content-Type", "application/json")
                                                   .body(json).asJson();

        return response.getBody().getObject();
    }

    public static BaseInfResponse callGetServices(String apiUrl, HashMap<String, Object> params, String interfCode, String token)  throws Exception{

        BaseInfResponse responseModel = new BaseInfResponse();

        // GET 发送请求
        HttpResponse<JsonNode> response = Unirest.get(apiUrl)
                                                 .header("interfcode", interfCode)
                                                 .queryString("access_token", token)
                                                 .queryString(params).asJson();


        try {
            //如果token过期
            if(response.getBody().getObject().has("error")){
                responseModel.setCode("error");
                responseModel.setMessage(response.getBody().getObject().getString("error"));
            }else{
                // 请求结果
                String code = response.getBody().getObject().getString("code");
                String message = response.getBody().getObject().getString("message");
                if(response.getBody().getObject().get("data") != null){
                    String data = response.getBody().getObject().get("data").toString();
                    responseModel.setData(data);
                }
                responseModel.setCode(code);
                responseModel.setMessage(message);
            }

            return responseModel;
        } catch (Exception e) {
            throw new ApplicationException(response.getBody().toString());
        }
    }

    public static void postService(String url, String json) throws Exception{

        // 发送请求
        Unirest.post(url).header("Content-Type", "application/json").body(json).asJson();
    }

    public static JSONObject saveOrUpdateUser(String url, String token, String json) throws Exception{

        // 发送请求
        HttpResponse<JsonNode> response = withTimeout(Unirest.post(url), TIME_OUT)
                                                 .header("Content-Type", "application/json")
                                                 .header("Authorization", token)
                                                 .body(json)
                                                 .asJson();

        JSONObject result = new JSONObject();

        try {

            result.put("resp_code", response.getBody().getObject().getString("resp_code"));
            result.put("resp_msg", response.getBody().getObject().getString("resp_msg"));
        } catch (Exception e) {
            result.put("resp_code", response.getBody().getObject().getString("status"));
            result.put("resp_msg", response.getBody().getObject().getString("message"));
        }

        return result;
    }

    public static JSONObject productStockAreaImport(String url, String token) throws Exception{

        // 发送请求
        HttpResponse<JsonNode> response = withTimeout(Unirest.post(url), TIME_OUT)
                                                 .header("Content-Type", "application/json")
                                                 .header("Authorization", token)
                                                 .asJson();

        JSONObject result = new JSONObject();

        try {
            result.put("resp_code", response.getBody().getObject().getString("resp_code"));
            result.put("resp_msg", response.getBody().getObject().getString("resp_msg"));
            result.put("data", response.getBody().getObject().get("data"));
        } catch (Exception e) {
            result.put("resp_code", response.getBody().getObject().getString("status"));
            result.put("resp_msg", response.getBody().getObject().getString("message"));
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    public static BaseInfResponse getCallIfs(JSONObject jsonObject, String apiUrl, String method, List apiList){

        BaseInfResponse response = new BaseInfResponse();

        jsonObject.put("siteid", CommonConstants.CHAR_DEFAULT_SITE_ID);
        jsonObject.put("system", CommonConstants.CHAR_DEFAULT_SYSTEM);
        jsonObject.put("method", method);
        jsonObject.put("data", apiList);

        try {
            response = callIfsServices(apiUrl, JSONObject.toJSONString(jsonObject));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    /**
     *  超时设置
     *
     * @param request Unirest 请求
     * @param timeout int 超时时间
     */
    private static <T extends HttpRequest<?>> T withTimeout(T request, int timeout) {
        request.socketTimeout(timeout)
               .connectTimeout(timeout);
        return request;
    }

    /**
     *
     * @param url String 路径
     * @return
     * @throws Exception
     */
    public static JSONObject getOpenIdByCode(String url) throws Exception{

        // 发送请求
        HttpResponse<JsonNode> response = withTimeout(Unirest.get(url), TIME_OUT).asJson();

        JSONObject result = new JSONObject();

        Boolean successFlag = response.getBody().getObject().toMap().containsKey("openid");

        if (successFlag) {
            result.put("openid", response.getBody().getObject().getString("openid"));
        } else {
            result.put("errcode", response.getBody().getObject().getString("errcode"));
            result.put("errmsg", response.getBody().getObject().getString("errmsg"));
        }

        return result;
    }

    public static JSONObject getNewIfsTokenService(String url, Map<String, Object> userMap) throws Exception{

        HttpResponse<JsonNode> response = withTimeout(Unirest.post(url), TIME_OUT) .header("accept", "application/json; charset=utf-8").fields(userMap).asJson();

        try {
            JSONObject result = new JSONObject();
            String accessToken = response.getBody().getObject().getString("access_token");
            String expiresIn = response.getBody().getObject().getString("expires_in");
            result.put("access_token", accessToken);
            result.put("expires_in", expiresIn);
            return result;
        } catch (Exception e) {
            throw new ApplicationException(response.getBody().toString());
        }
    }

    public static BaseInfResponse callIfsServicesWithToken(String apiUrl, String json,String token)  throws Exception{

        // 超时设置
//        Unirest.config().reset().socketTimeout(TIME_OUT).connectTimeout(TIME_OUT);

        BaseInfResponse responseModel = new BaseInfResponse();

        // 发送请求
        HttpResponse<JsonNode> response = withTimeout(Unirest.post(apiUrl), 60000)
                                                   .header("Content-Type", "application/json")
                                                   .queryString("access_token", token)
                                                   .body(json).asJson();

        // 请求结果
        String code = response.getBody().getObject().getString("code");
        String message = response.getBody().getObject().getString("message");
        if(response.getBody().getObject().get("data") != null){
            String data = response.getBody().getObject().get("data").toString();
            responseModel.setData(data);
        }

        responseModel.setCode(code);
        responseModel.setMessage(message);

        return responseModel;
    }

    public static BaseInfResponse callFamilyServices(String apiUrl, String json)  throws Exception{

        BaseInfResponse responseModel = new BaseInfResponse();

      // 发送请求
      HttpResponse<JsonNode> response = withTimeout(Unirest.post(apiUrl), 300000)
                                                 .header("Content-Type", "application/json")
                                                 .body(json).asJson();

      // 请求结果
      JSONObject result = (JSONObject) JSONObject.parse(response.getBody().getObject().toString());
      JSONObject data = (JSONObject) JSONObject.parse(result.getString("data"));
      String code = data.getString("status");
      String message = data.getString("message");

      responseModel.setCode(code);
      responseModel.setMessage(message);

      return responseModel;
    }
}
