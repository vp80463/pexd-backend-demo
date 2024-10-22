package com.a1stream.web.app.config.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.ymsl.solid.web.event.LogoutApplicationEvent;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Publish a LogoutApplicationEvent when a user logs out.
 */
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication != null) {
            applicationContext.publishEvent(new LogoutApplicationEvent(this, authentication.getName()));
        }
        response.getWriter().write("Logged out");
    }
}
