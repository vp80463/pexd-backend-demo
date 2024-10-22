package com.blank;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.ymsl.solid.jpa.repository.JpaExtensionRepositoryFactoryBean;


/**
 *
 * @author YMSLX
 * @version 1.0
 *
 */

@TestConfiguration
@ComponentScan(basePackages = {"com.blank.service", "com.blank.facade"})
@EntityScan("com.blank.entity")
@EnableJpaRepositories(basePackages = {"com.blank.repository"}, repositoryFactoryBeanClass = JpaExtensionRepositoryFactoryBean.class)
@SpringBootConfiguration
@ImportAutoConfiguration({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class DomainTestConfiguration {
}
