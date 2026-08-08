package com.valerius.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the application root path.
 *
 * @author Valerius
 */
@Controller
public class HomeController {

    /**
     * Redirects clients from the site root to the blog creation page.
     *
     * @return a redirect view name targeting {@code /create}
     */
    @GetMapping("/")
    private String home() {
        return "redirect:/create";
    }

}
