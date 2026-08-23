package com.radartech.controller;

import com.radartech.domain.AppUser;
import com.radartech.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;

    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("viewer")
    public AppUser viewer(Authentication authentication) {
        return userService.currentUser(authentication).orElse(null);
    }
}
