package com.a1stream.common.ifs.messageing;

/**
 * AmazonSQSTimeoutException
 *
 */
public class AmazonSQSIllegalStateException extends AmazonSQSException {

    private static final long serialVersionUID = 8262299295390131327L;

    public AmazonSQSIllegalStateException(String message) {
        super(message);
    }

    public AmazonSQSIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
