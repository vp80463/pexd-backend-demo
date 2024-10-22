package com.a1stream.web.app.config.auth;

import com.ymsl.solid.base.json.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler when user login failed.
 * Write error messages to response.
 */
public class UserLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        Map<String, String> result = new HashMap<>();
        if (!(exception instanceof BadCredentialsException)) {
            result.put("code", "403");
            response.setStatus(403);
        } else {
            result.put("code", "401");
            response.setStatus(401);
        }
        result.put("exception", exception.getMessage());

        response.getWriter().write(JsonUtils.toString(result));
    }
}
