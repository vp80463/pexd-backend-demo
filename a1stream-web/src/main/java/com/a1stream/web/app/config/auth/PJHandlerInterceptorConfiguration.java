package com.a1stream.web.app.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.a1stream.web.app.handler.MenuCheckInterceptor;


/**
* 功能描述:添加update-product拦截配置
*
* @author mid1955
*/
@Configuration
@SuppressWarnings("all")
public class PJHandlerInterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private MenuCheckInterceptor menuCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FunctionNameCaptureInterceptor());
        registry.addInterceptor(menuCheckInterceptor)
                    .addPathPatterns("/**/unit/**")
                    .addPathPatterns("/**/parts/**")
                    .addPathPatterns("/**/service/**")
                    .addPathPatterns("/**/master/**");
    }
}
