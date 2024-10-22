package com.a1stream.common.ifs.messageing;

/**
 * AmazonSQSTimeoutException
 *
 */
public class AmazonSQSTimeoutException extends AmazonSQSException {

    private static final long serialVersionUID = -7714151748309899287L;

    public AmazonSQSTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmazonSQSTimeoutException(String message) {
        super(message);
    }

    public AmazonSQSTimeoutException(Throwable cause) {
        super(cause);
    }

}
