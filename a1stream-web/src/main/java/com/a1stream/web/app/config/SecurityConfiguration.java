package com.a1stream.web.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.a1stream.web.app.config.auth.DummyAuthenticationInterceptor;
import com.a1stream.web.app.config.auth.PJUserDetailsServiceLoader;

/**
 * Basic security configuration
 */
@Configuration
public class SecurityConfiguration {


    /**
     * declaring no-authentication resources and x-frame-option settings
     */

    @Bean("commonWebSecurityConfig")
    @Order(SecurityProperties.IGNORED_ORDER + 100)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/**/webjars/**","/**/resources/**","/**/ynawebjars/**","/**/public/**", "/**/static-res/**", "/monitor/**");
    }

    /**
     * Dummy authentication. Used for testing interfaces which require user information.
     */
    @Configuration
    @Profile("dummy-auth")
    @Order(SecurityProperties.BASIC_AUTH_ORDER + 2)
    protected class DummySecurity implements WebMvcConfigurer {

        @Value("${solid.pj.login.dummy.allow-origins}")
        private String[] dummyAllowOrigins;

        @Bean
        @Order(SecurityProperties.BASIC_AUTH_ORDER + 1)
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.securityMatcher("/**")
                    .authorizeHttpRequests(authz ->
                            authz.anyRequest().permitAll()
                    );

            http.addFilterBefore(dummyAuthenticationInterceptor(), UsernamePasswordAuthenticationFilter.class);
            http.csrf(AbstractHttpConfigurer::disable);
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
            http.cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource()));

            return http.build();
        }

        @Bean
        public PJUserDetailsServiceLoader userDetailsService() {
          return new PJUserDetailsServiceLoader();
        }

        @Bean
        public DummyAuthenticationInterceptor dummyAuthenticationInterceptor() {
            return new DummyAuthenticationInterceptor();
        }

        @Bean
        public CorsFilter corsFilter() {
            return new CorsFilter(urlBasedCorsConfigurationSource());
        }

        private UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            for(String allowOrigins: dummyAllowOrigins) {
                config.addAllowedOriginPattern(allowOrigins);
            }
            config.addAllowedHeader(CorsConfiguration.ALL);
            config.addAllowedMethod(CorsConfiguration.ALL);
            source.registerCorsConfiguration("/**", config); // api to get access token
            return source;
        }

    }



}
