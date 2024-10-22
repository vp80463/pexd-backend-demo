package com.a1stream.web.app.controller.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ymsl.solid.web.usercontext.UserDetailsAccessor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Tag(name = "ValidateController", description = "validate user login status")
public class ValidateController {

    public static final String VALIDATE_URL = "/validate";

    @Operation(summary = "Validate user login status", security = {@SecurityRequirement(name = "X-Auth-Token")})
    @PostMapping(VALIDATE_URL)
    public String validate(HttpServletResponse response) {
        if(UserDetailsAccessor.DEFAULT.get() != null) {
            return "valid";
        } else {
            response.setStatus(401);
            return "invalid";
        }
    }
}
