package com.a1stream.web.app.config.auth;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Use for returning 401 status code to browser instead of 302 when accessing api before login.
 * When necessary, set {@code http.exceptionHandling().authenticationEntryPoint(new CustomLoginUrlAuthenticationEntryPoint("/login"))} in {@link PJLoginSecurityConfiguration}
 */
public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // set 401 for post requests
        if(HttpMethod.valueOf(request.getMethod()) == HttpMethod.POST) {
            response.sendError(401, "Access Denied!");
        } else {
            super.commence(request, response, authException);
        }

    }
}
