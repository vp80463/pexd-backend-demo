package com.a1stream.common.ifs.messageing;

/**
 * AmazonSQSTimeoutException
 *
 */
public class AmazonSQSException extends RuntimeException {

    private static final long serialVersionUID = -6130213685322435026L;

    public AmazonSQSException(String message) {
        super(message);
    }

    public AmazonSQSException(Throwable cause) {
        super(cause);
    }

    public AmazonSQSException(String message, Throwable cause) {
        super(message, cause);
    }

}
