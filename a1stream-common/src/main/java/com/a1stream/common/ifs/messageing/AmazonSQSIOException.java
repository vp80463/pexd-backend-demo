package com.a1stream.common.ifs.messageing;

import java.io.IOException;

/**
 * AmazonSQSTimeoutException
 *
 */
public class AmazonSQSIOException extends AmazonSQSException {

    private static final long serialVersionUID = 5403485829602100630L;

    public AmazonSQSIOException(IOException cause) {
        super(cause);
    }

    public AmazonSQSIOException(String message, Throwable cause) {
        super(message, cause);
    }

}
