package com.a1stream.web.app.controller.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.web.app.form.UserInfoModel;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import com.ymsl.solid.web.usercontext.UserDetailsAccessor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for user info.
 */
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "user info")
public class UserController implements RestProcessAware {

    @Operation(summary = "Get user info", security = {@SecurityRequirement(name = "X-Auth-Token")})
    @PostMapping("/info")
    public UserInfoModel userinfo(HttpServletResponse response) {
        if(UserDetailsAccessor.DEFAULT.get() != null) {
            PJUserDetails ud = UserDetailsAccessor.DEFAULT.get();
            UserInfoModel uc = BeanMapUtils.mapTo(ud, UserInfoModel.class);
            return uc;
        } else {
            // if no user exists, return null for saving traffic
            response.setStatus(401);
            return null;

        }

    }
}
