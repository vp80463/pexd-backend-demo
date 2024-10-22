package com.a1stream.web.app.config.auth;

import com.ymsl.solid.base.json.JsonUtils;
import com.ymsl.solid.base.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler when login succeeded.
 * If request url has been memorized, return as redirect url inside response body.
 */
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        Map<String, String> result = new HashMap<>();
        String redirectUrl = request.getContextPath() + "/";

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if(savedRequest != null) {
            if(savedRequest instanceof DefaultSavedRequest dsr) {
                redirectUrl = dsr.getRequestURI();
                if(StringUtils.isNotEmpty(dsr.getQueryString())) {
                    redirectUrl = redirectUrl + "?" + dsr.getQueryString();
                }
            } else {
                redirectUrl = savedRequest.getRedirectUrl();
            }
        }

        result.put("redirectUri", redirectUrl);
        response.getWriter().write(JsonUtils.toString(result));
    }
}
