package com.a1stream.common.ifs.messageing.listener;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Amazon SQS Accessor
 *
 */
public class AmazonSQSAccessor implements InitializingBean {

    private volatile SqsClient amazonSQS;

    public final void setAmazonSQS(SqsClient amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public SqsClient getAmazonSQS() {
        return amazonSQS;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.amazonSQS, "amazonSQS is required");
    }

    protected RuntimeException convertAmazonSQSAccessorException(Exception ex) {
        return AmazonSQSExceptionTranslator.convertAmazonSQSAccessException(ex);
    }

}
