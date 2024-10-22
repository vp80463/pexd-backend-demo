package com.a1stream.common.ifs.messageing;

/**
 * AmazonSQSTimeoutException
 *
 */
public class AmazonSQSStartupException extends AmazonSQSException {

    private static final long serialVersionUID = 1192588113074428588L;

    public AmazonSQSStartupException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
