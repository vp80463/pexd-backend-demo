package com.a1stream.common.ifs.messageing;

/**
 * UncategorizedAmasonSQSException
 *
 */
public class UncategorizedAmazonSQSException extends AmazonSQSException {

    private static final long serialVersionUID = 569780504105597881L;

    public UncategorizedAmazonSQSException(Throwable cause) {
        super(cause);
    }

    public UncategorizedAmazonSQSException(String message, Throwable cause) {
        super(message, cause);
    }

}
