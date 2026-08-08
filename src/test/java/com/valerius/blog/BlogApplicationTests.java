package com.valerius.blog;

import com.valerius.blog.repository.UserRepository;
import com.valerius.blog.security.OAuthAccountService;
import com.valerius.blog.security.OAuth2LoginSuccessHandler;
import com.valerius.blog.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Context-load smoke tests for {@link BlogApplication}.
 *
 * @author Valerius
 */
@SpringBootTest
@AutoConfigureMockMvc
class BlogApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthAccountService oauthAccountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    /**
     * Fails if the application context cannot start.
     */
    @Test
    void contextLoads() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"google", "github"})
    void authorizationStartsOAuthFlow(String provider) throws Exception {
        mockMvc.perform(get("/oauth2/authorization/{provider}", provider))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location",
                        startsWith(provider.equals("google")
                                ? "https://accounts.google.com/o/oauth2/v2/auth?"
                                : "https://github.com/login/oauth/authorize?")));
    }

    @Test
    void oauthAccountCreationStoresAnUnusablePassword() {
        String email = "oauth-test@example.com";

        var user = oauthAccountService.findOrCreate(email);

        assertThat(user.getPassword()).isNotBlank();
        assertThat(userRepository.findByEmail(email)).contains(user);
    }

    @Test
    void oauthCallbackCreatesAnApplicationSession() throws Exception {
        String email = "callback-oauth@example.com";
        var principal = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of("email", email), "email");
        var oauthAuthentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        oauth2LoginSuccessHandler.onAuthenticationSuccess(
                request, response, oauthAuthentication);

        SecurityContext context = (SecurityContext) request.getSession().getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(response.getRedirectedUrl()).isEqualTo("/create");
        assertThat(response.getCookie("remember-me")).isNotNull();
        assertThat(context.getAuthentication().getPrincipal()).isInstanceOf(User.class);
        assertThat(((User) context.getAuthentication().getPrincipal()).getEmail())
                .isEqualTo(email);
        assertThat(userRepository.findByEmail(email)).isPresent();
    }

}
