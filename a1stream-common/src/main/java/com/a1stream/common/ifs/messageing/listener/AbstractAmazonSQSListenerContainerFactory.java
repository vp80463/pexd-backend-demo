package com.a1stream.common.ifs.messageing.listener;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * 抽象 Amazon SQS 监听容器创建工厂类
 * @param <C> Amazon SQS 监听容器
 *
 */
public abstract class AbstractAmazonSQSListenerContainerFactory<C extends AbstractMessageListenerContainer>
        implements AmazonSQSListenerContainerFactory<C>, InitializingBean {

    private SqsClient amazonSQS;

    private Executor taskExecutor;

    private Integer prefetchCount;

    public void setAmazonSQS(SqsClient amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setPrefetchCount(Integer prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

    @Override
    public C createListenerContainer(String queueName,int waitTimeSeconds, int maxNumberOfMessages, MessageListener messageListener) {
        C instance = createContainerInstance();
        if(this.amazonSQS != null) {
            instance.setAmazonSQS(amazonSQS);
        }
        if(this.taskExecutor != null) {
            instance.setTaskExecutor(taskExecutor);
        }
        if(this.prefetchCount != null){
            instance.setPrefetchCount(prefetchCount);
        }
        instance.setQueueName(queueName);
        instance.setWaitTimeSeconds(waitTimeSeconds);
        instance.setMaxNumberOfMessages(maxNumberOfMessages);
        instance.setMessageListener(messageListener);
        initializeContainer(instance);
        return instance;
    }

    @Override
    public void afterPropertiesSet(){
        Assert.notNull(amazonSQS, "amazon sqs info can not be null");
    }

    protected abstract C createContainerInstance();

    protected void initializeContainer(C instance) {
    }

}
