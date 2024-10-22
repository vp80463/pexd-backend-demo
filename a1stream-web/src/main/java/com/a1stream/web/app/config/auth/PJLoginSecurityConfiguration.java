package com.a1stream.web.app.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.a1stream.web.app.controller.test.ValidateController;
import com.ymsl.solid.web.authority.BaseAuthenticationDetailsSource;
import com.ymsl.solid.web.authority.BaseUserDetailsAuthenticationProvider;

/**
 * Security configuration for single login(manage username and password by app)
 */
@Configuration
//@Profile("single-login")
@Profile("login-security")
public class PJLoginSecurityConfiguration {

  private static final String LOGIN_URL = "/";

  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new BaseUserDetailsAuthenticationProvider();
  }

  @Bean
  public PJUserDetailsServiceLoader userDetailsService() {
    return new PJUserDetailsServiceLoader();
  }

  private BaseAuthenticationDetailsSource baseAuthenticationDetailsSource;

  @Autowired
  public void setBaseAuthenticationDetailsSource(BaseAuthenticationDetailsSource baseAuthenticationDetailsSource) {
    this.baseAuthenticationDetailsSource = baseAuthenticationDetailsSource;
  }

  @Bean
  public UserLogoutSuccessHandler userLogoutSuccessHandler() {
    return new UserLogoutSuccessHandler();
  }

  @Bean
  @Profile("login-security")
  @Order(SecurityProperties.BASIC_AUTH_ORDER + 3)
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(authz ->
      authz.requestMatchers("/error", LOGIN_URL, ValidateController.VALIDATE_URL, "/user/info").permitAll().anyRequest()
                      .authenticated());

    http.sessionManagement(sessionManagement -> sessionManagement.enableSessionUrlRewriting(false));

    http.logout(logout -> {
      logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
      logout.logoutSuccessHandler(userLogoutSuccessHandler());
    });

    http.authenticationProvider(authenticationProvider());

    http.csrf(AbstractHttpConfigurer::disable);
    http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
    return http.build();
  }

  @Configuration
  @Profile("login-security")
  @Order(Ordered.HIGHEST_PRECEDENCE + 10)
  protected static class InMemoryAuthenticationSecurity
      extends GlobalAuthenticationConfigurerAdapter {

    AuthenticationProvider authenticationProvider;

    @Autowired
    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
      this.authenticationProvider = authenticationProvider;
    }

    /** Definition of Spring Security authentication success conditions. */
    @Override
    public void init(final AuthenticationManagerBuilder auth) {
      auth.authenticationProvider(authenticationProvider);
    }
  }
}
