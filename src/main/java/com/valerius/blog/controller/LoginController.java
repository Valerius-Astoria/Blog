package com.valerius.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the custom sign-in page at {@code /login}.
 * <p>
 * Form submission is handled by Spring Security at {@code POST /login}.
 * Failed attempts redirect back with {@code ?error}; logout redirects
 * with {@code ?logout}.
 *
 * @author Valerius
 */
@Controller
public class LoginController {

    /**
     * Shows the login form.
     *
     * @return view name {@code login}
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
