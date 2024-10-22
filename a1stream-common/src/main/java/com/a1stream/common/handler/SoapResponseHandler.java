package com.a1stream.common.handler;


import com.a1stream.common.model.InvoiceSoapResponseResult;

/**
 * @author dong zhen
 */
public interface SoapResponseHandler {
    static final String KEYWORD_ERR = "ERR";

    default InvoiceSoapResponseResult handleResponseText(String  responseText) {
        //It's error
        if(responseText != null && responseText.startsWith(KEYWORD_ERR)) {
            return processFailure(responseText);
        }

        //When it's successful.
        return processSuccess(responseText);
    }

    InvoiceSoapResponseResult processSuccess(String  responseText);

    InvoiceSoapResponseResult processFailure(String  responseText);
}
