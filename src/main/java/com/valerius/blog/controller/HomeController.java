package com.valerius.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Maps the site root to the create-post flow.
 * <p>
 * A GET to {@code /} redirects to {@code /create}.
 *
 * @author Valerius
 * @see CreateBlogController
 */
@Controller
public class HomeController {

    /**
     * Redirects {@code /} to {@code /create}.
     *
     * @return redirect view name for {@code /create}
     */
    @GetMapping("/")
    private String home() {
        return "redirect:/create";
    }

}
