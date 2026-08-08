package com.valerius.blog.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 * Skips remember-me for OAuth principals during the OAuth callback filter.
 * OAuth remember-me is issued from {@link OAuth2LoginSuccessHandler} after
 * the local {@link com.valerius.blog.model.User} account exists.
 */
public class OAuthAwareRememberMeServices implements RememberMeServices {

    private final TokenBasedRememberMeServices delegate;

    public OAuthAwareRememberMeServices(
            String rememberMeKey,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        this.delegate = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        this.delegate.setAlwaysRemember(true);
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        return delegate.autoLogin(request, response);
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        delegate.loginFail(request, response);
    }

    @Override
    public void loginSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication successfulAuthentication) {
        Object principal = successfulAuthentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            return;
        }
        delegate.loginSuccess(request, response, successfulAuthentication);
    }

    public void issueRememberMeCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        delegate.loginSuccess(request, response, authentication);
    }
}
