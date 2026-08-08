package com.valerius.blog.controller;

import com.valerius.blog.model.Blog;
import com.valerius.blog.model.User;
import com.valerius.blog.repository.BlogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Serves the signed-in user's post archive at {@code /history}.
 * <p>
 * List and detail views include only blogs that list the current user
 * as an author. Detail requests for unknown or unauthorized ids yield
 * {@link HttpStatus#NOT_FOUND}.
 *
 * @author Valerius
 * @see BlogRepository
 */
@Controller
@RequestMapping("/history")
public class HistoryController {

    private final BlogRepository blogRepository;

    /**
     * @param blogRepository blog queries; must not be {@code null}
     */
    public HistoryController(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    /**
     * Shows all posts authored by the current user.
     * <p>
     * Model attribute {@code blogs} is the matching list; never
     * {@code null}, empty when the user has no posts.
     *
     * @param authentication current {@link Authentication}; unused by
     *                       this method
     * @param model          view model; must not be {@code null}
     * @param user           authenticated account; must not be
     *                       {@code null}
     * @return view name {@code history}
     */
    @GetMapping
    public String history(Authentication authentication, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("blogs",
                blogRepository.findByAuthorsContaining(user));
        return "history";
    }

    /**
     * Shows one post authored by the current user.
     * <p>
     * Model attribute {@code blog} is set when the id exists and the
     * current user is among its authors.
     *
     * @param id             blog id; must not be {@code null}
     * @param user           authenticated account; must not be
     *                       {@code null}
     * @param authentication current {@link Authentication}; unused by
     *                       this method
     * @param model          view model; must not be {@code null}
     * @return view name {@code history-detail}
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND}
     *         when no matching authored blog exists
     */
    @GetMapping("/{id}")
    public String historyDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Authentication authentication,
            Model model) {
        Blog blog = blogRepository.findByIdAndAuthorsContaining(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No such record in your archive."));
        model.addAttribute("blog", blog);
        return "history-detail";
    }
}
