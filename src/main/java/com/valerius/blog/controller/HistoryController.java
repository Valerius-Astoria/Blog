package com.valerius.blog.controller;

import com.valerius.blog.model.Blog;
import com.valerius.blog.model.User;
import com.valerius.blog.repository.BlogRepository;
import com.valerius.blog.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles browsing of the authenticated user's published blog posts
 * under {@code /History}.
 * <p>
 * The list view exposes every blog that includes the current user among
 * its authors. The detail view returns a single such blog by id, or
 * responds with {@link HttpStatus#NOT_FOUND} when the id is unknown or
 * not authored by the current user.
 *
 * @author Valerius
 * @see BlogRepository
 * @see CurrentUserService
 */
@Controller
@RequestMapping("/History")
public class HistoryController {

    private final BlogRepository blogRepository;
    private final CurrentUserService currentUserService;

    /**
     * Creates a controller that loads history from the given
     * repository and user service.
     *
     * @param blogRepository     repository used to find blogs by
     *                           author; must not be {@code null}
     * @param currentUserService service used to resolve the
     *                           authenticated account; must not be
     *                           {@code null}
     */
    public HistoryController(BlogRepository blogRepository,
            CurrentUserService currentUserService) {
        this.blogRepository = blogRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Displays the authenticated user's blog archive.
     * <p>
     * Adds the attribute {@code blogs} to the model: the list of posts
     * whose authors include the current user. The list is empty when
     * the user has no posts; it is never {@code null}.
     *
     * @param authentication the current authentication; must not be
     *                       {@code null}
     * @param model          the view model to receive {@code blogs};
     *                       must not be {@code null}
     * @return the logical view name {@code history}
     * @throws IllegalStateException if the authenticated principal
     *         has no matching persisted user
     */
    @GetMapping
    public String history(Authentication authentication, Model model) {
        User user = currentUserService.require(authentication);
        model.addAttribute("blogs",
                blogRepository.findByAuthorsContaining(user));
        return "history";
    }

    /**
     * Displays one blog from the authenticated user's archive.
     * <p>
     * Adds the attribute {@code blog} when a post with the given
     * {@code id} exists and includes the current user among its
     * authors.
     *
     * @param id             the blog identifier
     * @param authentication the current authentication; must not be
     *                       {@code null}
     * @param model          the view model to receive {@code blog};
     *                       must not be {@code null}
     * @return the logical view name {@code history-detail}
     * @throws IllegalStateException if the authenticated principal
     *         has no matching persisted user
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND}
     *         if no matching blog exists for this user
     */
    @GetMapping("/{id}")
    public String historyDetail(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {
        User user = currentUserService.require(authentication);
        Blog blog = blogRepository.findByIdAndAuthorsContaining(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No such record in your archive."));
        model.addAttribute("blog", blog);
        return "history-detail";
    }
}
