package com.a1stream.domain_mi.config;

///**
// * description: 多数据源配置类。mi数据源
// * {@code @EnableJpaRepositories:  basePackages 指定到自己的repository。}
// */
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = {
//                                        "com.a1stream.domain_mi.repository"
//                                      , "com.a1stream.domain_mi.custom"
//                         },
//                       entityManagerFactoryRef = "miDbEntityManagerFactory",
//                       transactionManagerRef = "miDbTransactionManager",
//                       repositoryBaseClass = JpaExtensionRepositoryImpl.class)
//public class MultipleDataSourcesMiConfig {
//
//    /**
//     * 配置数据源
//     * @return
//     */
//    @Bean("miDataSource")
//    @ConfigurationProperties(prefix = ConfigConstants.PJ_DATASOURCE_PREFIX  + ".mi-db")
//    public DataSource secondaryDataSource(){
//        return DataSourceBuilder.create().build();
//    }
//
//    /**
//     * 实体管理工厂的bean
//     * @param builder
//     * @return
//     */
//    @Bean(name = "miDbEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean miDbEntityManagerFactory(EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(secondaryDataSource())
//                .packages("com.a1stream.domain_mi.entity")
//                .persistenceUnit("miDb")
//                .build();
//    }
//
//    /**
//     * 配置事务管理
//     * @param builder
//     * @return
//     */
//    @Bean(name = "miDbTransactionManager")
//    public PlatformTransactionManager miDbTransactionManager(EntityManagerFactoryBuilder builder) {
//        return new JpaTransactionManager(Objects.requireNonNull(miDbEntityManagerFactory(builder).getObject()));
//    }
//
//}