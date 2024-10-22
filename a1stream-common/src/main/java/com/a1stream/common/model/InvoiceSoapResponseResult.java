package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class InvoiceSoapResponseResult {

    private boolean isSuccess;

    private String message;

    public static InvoiceSoapResponseResult success(String msg) {
        InvoiceSoapResponseResult responseResult = new InvoiceSoapResponseResult();
        responseResult.setSuccess(true);
        responseResult.setMessage(msg);
        return responseResult;
    }

    public static InvoiceSoapResponseResult failure(String reason) {
        InvoiceSoapResponseResult responseResult = new InvoiceSoapResponseResult();
        responseResult.setSuccess(false);
        responseResult.setMessage(reason);
        return responseResult;
    }
}
