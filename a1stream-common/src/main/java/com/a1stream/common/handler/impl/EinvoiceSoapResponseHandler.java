package com.a1stream.common.handler.impl;


import com.a1stream.common.handler.SoapResponseHandler;
import com.a1stream.common.model.InvoiceSoapResponseResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dong zhen
 */
public class EinvoiceSoapResponseHandler implements SoapResponseHandler {

    public static final EinvoiceSoapResponseHandler INSTANCE = new EinvoiceSoapResponseHandler();

    private static final Map<String, String> EINVOICE_ERR_MAP = new HashMap<String, String>(6);

    static {
        EINVOICE_ERR_MAP.put("ERR:1", "Tài khoản sai hoặc không có quyền");//Wrong account or doesn’t have permission
        EINVOICE_ERR_MAP.put("ERR:3", "Dữ liệu XML bị sai");//XML data is invalid
        EINVOICE_ERR_MAP.put("ERR:5", "Không thể phát hành hóa đơn");//Can’t publish invoice
        EINVOICE_ERR_MAP.put("ERR:6", "Hết hóa đơn");//Doesn’t have enough number of invoices for batch publish
        EINVOICE_ERR_MAP.put("ERR:10", "Số lượng hóa đơn quá nhiều");//XML data has to many invoices
        EINVOICE_ERR_MAP.put("ERR:20", "Sai mẫu HĐ và Serial");//Wrong Pattern and serial
    }

    @Override
    public InvoiceSoapResponseResult processSuccess(String responseText) {
        return InvoiceSoapResponseResult.success(responseText);
    }

    @Override
    public InvoiceSoapResponseResult processFailure(String responseText) {
        String key = EINVOICE_ERR_MAP.containsKey(responseText) ? responseText :  null;
        String errMsg = (key == null) ? "" : key + "_" + EINVOICE_ERR_MAP.get(key);
        return InvoiceSoapResponseResult.failure(errMsg);
    }
}
