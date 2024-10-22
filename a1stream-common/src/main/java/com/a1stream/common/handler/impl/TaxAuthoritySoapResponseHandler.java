package com.a1stream.common.handler.impl;


import com.a1stream.common.handler.SoapResponseHandler;
import com.a1stream.common.model.InvoiceSoapResponseResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dong zhen
 */
public class TaxAuthoritySoapResponseHandler implements SoapResponseHandler {

    public static final TaxAuthoritySoapResponseHandler INSTANCE = new TaxAuthoritySoapResponseHandler();
    private static final Map<String, String> TAX_AUTH_ERR_MAP = new HashMap<String, String>(5);
    private static final String UNKNOWN_ERR_KEY = "ERR:5";
    private static final Pattern REG_PATTERN_BILLNO  = Pattern.compile("<MCCQThue>(.*)</MCCQThue>", Pattern.MULTILINE);

    static {
        TAX_AUTH_ERR_MAP.put("ERR:1", "Wrong login account or do not have permission to add new invoices");
        TAX_AUTH_ERR_MAP.put("ERR:2", "The corresponding invoice could not be found");
        TAX_AUTH_ERR_MAP.put("ERR:10", "Exceeding the amount of 100 bills to get");
        TAX_AUTH_ERR_MAP.put("ERR:20", "Can't get user information");
        TAX_AUTH_ERR_MAP.put(UNKNOWN_ERR_KEY, "Unknown error, could not get the invoice data with the tax authorities code according to the input data");
    }

    @Override
    public InvoiceSoapResponseResult processSuccess(String responseText) {
        byte[] data = Base64.getDecoder().decode(responseText);
        String xml = new String(data, StandardCharsets.UTF_8);
        String foundNo = findNo(xml);
        return InvoiceSoapResponseResult.success(foundNo);
    }

    @Override
    public InvoiceSoapResponseResult processFailure(String responseText) {
        String key = TAX_AUTH_ERR_MAP.containsKey(responseText) ? responseText : UNKNOWN_ERR_KEY;
        String errMsg = key + "_" + TAX_AUTH_ERR_MAP.get(key);
        return InvoiceSoapResponseResult.failure(errMsg);
    }

    private String findNo(String s) {
        Matcher m  = REG_PATTERN_BILLNO.matcher(s);
        if(m.find()) {
            return m.group(1);
        }
        return "";
    }
}
