package com.a1stream.common.ifs.messageing.listener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import org.springframework.util.Assert;

import com.a1stream.common.ifs.messageing.AmazonSQSConnectException;
import com.a1stream.common.ifs.messageing.AmazonSQSException;
import com.a1stream.common.ifs.messageing.AmazonSQSIOException;
import com.a1stream.common.ifs.messageing.AmazonSQSTimeoutException;
import com.a1stream.common.ifs.messageing.AmazonSQSUnsupportedEncodingException;
import com.a1stream.common.ifs.messageing.UncategorizedAmazonSQSException;

/**
 * Amazon SQS 异常转换器
 *
 */
public class AmazonSQSExceptionTranslator {

    public static AmazonSQSException convertAmazonSQSAccessException(Throwable ex) {
        Assert.notNull(ex, "Exception must not be null");
        if (ex instanceof AmazonSQSException) {
            return (AmazonSQSException) ex;
        }
        if (ex instanceof ConnectException) {
            return new AmazonSQSConnectException((ConnectException) ex);
        }
        if (ex instanceof UnsupportedEncodingException) {
            return new AmazonSQSUnsupportedEncodingException(ex);
        }
        if (ex instanceof IOException) {
            return new AmazonSQSIOException((IOException) ex);
        }
        if (ex instanceof TimeoutException) {
            return new AmazonSQSTimeoutException(ex);
        }
        // fallback
        return new UncategorizedAmazonSQSException(ex);
    }

}
