package com.valerius.blog.controller;

import com.valerius.blog.model.Blog;
import com.valerius.blog.repository.BlogRepository;
import com.valerius.blog.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Handles creation of new {@link Blog} posts under {@code /create}.
 * <p>
 * GET requests render the creation form. POST requests validate the
 * submitted blog, attach the authenticated user as an author, persist
 * the entity, and redirect back to the creation form on success.
 *
 * @author Valerius
 * @see BlogRepository
 * @see CurrentUserService
 */
@Controller
@RequestMapping("/create")
public class CreateBlogController {

    private final BlogRepository blogRepository;
    private final CurrentUserService currentUserService;

    /**
     * Creates a controller that persists blogs and resolves the
     * current user.
     *
     * @param blogRepository     repository used to save blog posts;
     *                           must not be {@code null}
     * @param currentUserService service used to resolve the
     *                           authenticated account; must not be
     *                           {@code null}
     */
    public CreateBlogController(BlogRepository blogRepository,
            CurrentUserService currentUserService) {
        this.blogRepository = blogRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Supplies a fresh {@link Blog} instance for form binding.
     *
     * @return a new, unsaved blog; never {@code null}
     */
    @ModelAttribute("blog")
    public Blog blog() {
        return new Blog();
    }

    /**
     * Displays the blog creation form.
     *
     * @return the logical view name {@code create}
     */
    @GetMapping
    public String showBlogTemplate() {
        return "create";
    }

    /**
     * Validates and persists a submitted blog post.
     * <p>
     * If {@code errors} reports validation failures, redisplays the
     * creation form without saving. Otherwise adds the authenticated
     * user as an author, saves the blog, and redirects to
     * {@code /create}.
     *
     * @param blog           the submitted blog bound from the form;
     *                       must not be {@code null}
     * @param authentication the current authentication; must not be
     *                       {@code null} when this method is invoked
     *                       for a successful save path
     * @param sessionStatus  status of any session-related form
     *                       processing; must not be {@code null}
     * @param errors         binding and validation errors for
     *                       {@code blog}; must not be {@code null}
     * @return {@code create} when validation fails; otherwise a
     *         redirect to {@code /create}
     * @throws IllegalStateException if the authenticated principal
     *         has no matching persisted user
     */
    @PostMapping
    public String processBlog(
            @Valid @ModelAttribute("blog") Blog blog,
            Authentication authentication,
            SessionStatus sessionStatus,
            Errors errors) {

        if (errors.hasErrors()) {
            return "create";
        }

        blog.addAuthor(currentUserService.require(authentication));
        Blog saved = blogRepository.save(blog);
        sessionStatus.setComplete();

        return "redirect:/create";
    }
}
