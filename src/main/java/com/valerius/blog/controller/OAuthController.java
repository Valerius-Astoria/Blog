package com.valerius.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Convenience entry points that start OAuth2 authorization.
 * <p>
 * Spring Security processes the real authorization and callback at
 * {@code /oauth2/authorization/{registrationId}} and
 * {@code /login/oauth2/code/{registrationId}}. These mappings only
 * redirect into that flow.
 *
 * @author Valerius
 */
@Controller
@RequestMapping("/oauth2")
public class OAuthController {

    /**
     * Starts Google OAuth2/OIDC login.
     *
     * @return redirect to the Google authorization endpoint
     */
    @GetMapping("/google")
    public String google() {
        return "redirect:/oauth2/authorization/google";
    }

    /**
     * Starts GitHub OAuth2 login.
     *
     * @return redirect to the GitHub authorization endpoint
     */
    @GetMapping("/github")
    public String github() {
        return "redirect:/oauth2/authorization/github";
    }
}
