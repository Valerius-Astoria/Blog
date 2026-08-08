package com.valerius.blog.security;

import com.valerius.blog.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthAccountService accounts;
    private final OAuthAwareRememberMeServices rememberMeServices;
    private final HttpSessionSecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public OAuth2LoginSuccessHandler(
            OAuthAccountService accounts,
            OAuthAwareRememberMeServices rememberMeServices) {
        this.accounts = accounts;
        this.rememberMeServices = rememberMeServices;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        String email = extractEmail(authentication.getPrincipal());
        User user = accounts.findOrCreate(email);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(newAuth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        rememberMeServices.issueRememberMeCookie(request, response, newAuth);
        response.sendRedirect("/create");
    }
    private String extractEmail(Object principal) {
        if (principal instanceof OidcUser oidc) {
            return oidc.getEmail();
        }
        if (principal instanceof OAuth2User oauth2) {
            Object email = oauth2.getAttribute("email");
            if (email != null) {
                return email.toString();
            }
        }
        throw new IllegalStateException("Unexpected principal: " + principal);
    }
}
