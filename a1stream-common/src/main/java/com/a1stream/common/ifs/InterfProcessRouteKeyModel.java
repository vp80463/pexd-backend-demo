package com.a1stream.common.ifs;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterfProcessRouteKeyModel  {

    private String exchangeId;

    private String toAppId;

    private String routeKey;

    private String ContentPath;

    private List<JSONObject> bodyList;

    private JSONObject body;
}

