package com.valerius.blog.security;

import org.jspecify.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> EMAIL_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final RestClient restClient = RestClient.create();

    @Override
    public @Nullable OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(request);
        String email = resolveEmail(request);
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_required"),
                    "GitHub did not provide an email");
        }

        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                attributes,
                "email");
    }

    private @Nullable String resolveEmail(OAuth2UserRequest request) {
        // GitHub's profile email may be public but is not guaranteed to be
        // verified. The authenticated emails endpoint includes verification
        // metadata, so use it as the authoritative source.
        return fetchPrimaryEmail(request);
    }

    private @Nullable String fetchPrimaryEmail(OAuth2UserRequest request) {
        List<Map<String, Object>> emails;
        try {
            emails = restClient.get()
                    .uri("https://api.github.com/user/emails")
                    .header("Authorization", "Bearer " + request.getAccessToken().getTokenValue())
                    .header("Accept", "application/vnd.github+json")
                    .retrieve()
                    .body(EMAIL_LIST_TYPE);
        } catch (RestClientException ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_lookup_failed"),
                    "Could not retrieve the GitHub email address", ex);
        }
        if (emails == null) {
            return null;
        }
        for (Map<String, Object> entry : emails) {
            Object address = entry.get("email");
            if (address == null || address.toString().isBlank()
                    || !Boolean.TRUE.equals(entry.get("verified"))) {
                continue;
            }
            if (Boolean.TRUE.equals(entry.get("primary"))) {
                return address.toString();
            }
        }
        for (Map<String, Object> entry : emails) {
            Object address = entry.get("email");
            if (address != null && !address.toString().isBlank()
                    && Boolean.TRUE.equals(entry.get("verified"))) {
                return address.toString();
            }
        }
        return null;
    }
}
