package com.a1stream.common.ifs.messageing;

/**
 * AmazonSQSTimeoutException
 *
 */
public class AmazonSQSConnectException extends AmazonSQSException {

    private static final long serialVersionUID = -2051795845228258952L;

    public AmazonSQSConnectException(Exception cause) {
        super(cause);
    }

    public AmazonSQSConnectException(String message, Throwable cause) {
        super(message, cause);
    }

}
