package com.a1stream.common.ifs.messageing.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/*
 * Amazon Queue Config
 *
 */
@Configuration
public class AmazonSQSBootstrapConfiguration {

    @Bean(name = "cn.carlzone.amazon.sqs.messageing.annotation.innerAmazonSQSListenerAnnotationBeanPostProcessor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AmazonSQSListenerAnnotationBeanPostProcessor amazonSQSListenerAnnotationProcessor() {
        return new AmazonSQSListenerAnnotationBeanPostProcessor();
    }
}
