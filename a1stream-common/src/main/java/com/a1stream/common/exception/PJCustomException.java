package com.a1stream.common.exception;

import com.ymsl.solid.base.exception.ApplicationException;

public class PJCustomException extends ApplicationException {

    private static final long serialVersionUID = 2911146921050273957L;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public PJCustomException(String message) {
        super(message);
    }
}
