package com.a1stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.ymsl.solid.jpa.repository.JpaExtensionRepositoryFactoryBean;

/**
 *
 * @author YMSLX
 * @version 1.0
 *
 */
@SpringBootApplication
@EntityScan(basePackageClasses = {PjApplication.class})
@EnableJpaRepositories(basePackageClasses = {PjApplication.class}, repositoryFactoryBeanClass = JpaExtensionRepositoryFactoryBean.class)
@EnableAsync
@EnableCaching
public class PjApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PjApplication.class);
        app.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PjApplication.class);
    }
}
