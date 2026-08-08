package com.valerius.blog.controller;

import com.valerius.blog.model.Blog;
import com.valerius.blog.model.User;
import com.valerius.blog.repository.BlogRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Creates blog posts at {@code /create}.
 * <p>
 * Successful POSTs attach the authenticated user as an author, save the
 * post, and redirect to {@code /history}.
 *
 * @author Valerius
 * @see BlogRepository
 * @see HistoryController
 */
@Controller
@RequestMapping("/create")
public class CreateBlogController {

    private static final Logger log =
            LoggerFactory.getLogger(CreateBlogController.class);

    private final BlogRepository blogRepository;

    /**
     * @param blogRepository store for new posts; must not be
     *                       {@code null}
     */
    public CreateBlogController(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    /**
     * Provides a new unbound {@link Blog} for the create form.
     *
     * @return a fresh blog instance; never {@code null}
     */
    @ModelAttribute("blog")
    public Blog blog() {
        return new Blog();
    }

    /**
     * Shows the create form.
     *
     * @return view name {@code create}
     */
    @GetMapping
    public String showBlogTemplate() {
        return "create";
    }

    /**
     * Validates and saves a submitted post.
     * <p>
     * On validation failure, redisplays {@code create}. On success,
     * adds {@code user} as an author, persists the blog, and redirects
     * to {@code /history}.
     *
     * @param blog          bound form blog; must not be {@code null}
     * @param user          authenticated account from the security
     *                      context; must not be {@code null} on the
     *                      success path
     * @param authentication current {@link Authentication}; unused by
     *                       this method
     * @param sessionStatus form session status; must not be
     *                      {@code null}
     * @param errors        binding and validation errors; must not be
     *                      {@code null}
     * @return {@code create} on validation failure; otherwise a
     *         redirect to {@code /history}
     */
    @PostMapping
    public String processBlog(
            @Valid @ModelAttribute("blog") Blog blog,
            @AuthenticationPrincipal User user,
            Authentication authentication,
            SessionStatus sessionStatus,
            Errors errors) {

        if (errors.hasErrors()) {
            return "create";
        }

        blog.addAuthor(user);
        Blog saved = blogRepository.save(blog);

        log.info(
                "Blog submitted: id={}, title={}, subtitle={}, body={}",
                saved.getId(),
                saved.getTitle(),
                saved.getSubTitle(),
                saved.getBody()
        );

        sessionStatus.setComplete();
        return "redirect:/history";
    }
}
