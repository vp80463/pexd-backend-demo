package com.a1stream.common.ifs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.a1stream.common.ifs.messageing.listener.SimpleAmazonSQSListenerContainerFactory;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;


/**
 * Amazon SQS Config
 *
 * @see AmazonSQSConfig
 */
@Configuration
public class AmazonSQSConfig {

    @Value("${pluglins.aws.region}")
    private String region;

	@Bean
	public SqsClient realAmazonSQS() {
	    SqsClient sqs = SqsClient.builder()
                .region(Region.of(region))
                .build();
		return sqs;
	}

	@Bean(name = "amazonSQSListenerContainerFactory")
	public SimpleAmazonSQSListenerContainerFactory amazonSQSListenerContainerFactory(SqsClient amazonSQS){
		SimpleAmazonSQSListenerContainerFactory factory = new SimpleAmazonSQSListenerContainerFactory();
		factory.setAmazonSQS(amazonSQS);
		return factory;
	}

}
