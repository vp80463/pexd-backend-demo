package com.blank.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.a1stream.PjApplication;
import com.a1stream.common.PJRedisConfiguration;
import com.ymsl.plugins.i18n.config.I18nConfiguration;
import com.ymsl.plugins.userauth.config.UserauthConfiguration;
import com.ymsl.solid.jpa.repository.JpaExtensionRepositoryFactoryBean;
import com.ymsl.solid.websocket.config.WebSocketConfiguration;

/**
 * Custom spring boot start class.
 * Different from {@link BlankApplication}, this class is used for test.
 * So unnecessary modules can be excluded by using specific profile,
 * scanning only the specific packages, or excluding the unnecessary configurations.
 * The package path of this class is different from the original one,
 * so it would not be scanned by spring boot automatically, and special functions can be activated.
 */
@SpringBootApplication
@Import({TestSecurityConfiguration.class, PJRedisConfiguration.class})
@Profile("development-test")
@ComponentScan(basePackages = "com.blank.test")
@EntityScan(basePackages = "com.blank.domain.entity")
@EnableJpaRepositories(basePackages = "com.blank.domain.repository", repositoryFactoryBeanClass = JpaExtensionRepositoryFactoryBean.class)
@EnableAutoConfiguration(exclude = {WebSocketConfiguration.class, UserauthConfiguration.class, I18nConfiguration.class})
public class BlankTestApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PjApplication.class);
        app.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PjApplication.class);
    }
}
