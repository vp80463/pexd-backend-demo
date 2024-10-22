package com.a1stream.domain.config;

///**
// * description: 多数据源配置类。主数据源
// * {@code @EnableJpaRepositories:  basePackages 指定到自己的repository。}
// */
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = {
//                                        "com.a1stream.domain.repository"
//                                      , "com.a1stream.domain.custom"
//                         },
//                       entityManagerFactoryRef = "entityManagerFactory",
//                       transactionManagerRef = "transactionManager",
//                       repositoryBaseClass = JpaExtensionRepositoryImpl.class)
//public class MultipleDataSourcesDmsConfig {
//
//    /**
//     * 配置次 数据源
//     * @return
//     */
//    @Bean("dmsDataSource")
//    @Primary
//    @ConfigurationProperties(prefix = ConfigConstants.PJ_DATASOURCE_PREFIX  + ".a1stream-db")
//    public DataSource secondaryDataSource(){
//        return DataSourceBuilder.create().build();
//    }
//
//    /**
//     * 实体管理工厂的bean
//     * @param builder
//     * @return
//     */
//    @Bean
//    @Primary
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(secondaryDataSource())
//                .packages("com.a1stream.domain.entity"
//                        , "com.ymsl.plugins.fileresource.entity"
//                        , "com.ymsl.plugins.userauth.entity"
//                        , "com.ymsl.plugins.i18n.entity"
//                        , "com.ymsl.plugins.log.jpa.entity")
//                .persistenceUnit("dmsDb")
//                .build();
//    }
//
//    /**
//     * 配置事务管理
//     * @param builder
//     * @return
//     */
//    @Bean(name = "transactionManager")
//    public PlatformTransactionManager dmsDbTransactionManager(EntityManagerFactoryBuilder builder) {
//        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory(builder).getObject()));
//    }
//
//}