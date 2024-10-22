package com.a1stream.web.app.ifs.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Profile(value = "rabbitMq")
public class IfsQueueConfig {

    @Value("${ifs.listen.a1streamQueue}")
    public String a1streamQueueName;

    @Bean
    public Queue a1streamQueue() {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("x-max-priority", 10);
        return new Queue(a1streamQueueName, true,false,false,map);
    }
}
