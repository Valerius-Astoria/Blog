# Blog

A Spring Boot blogging app built to deepen hands-on practice with **user validation** and **Spring Security**, including form login and OAuth2 sign-in with Google and GitHub.

**Live site:** [https://blog-9gy5.onrender.com](https://blog-9gy5.onrender.com)

---

## Purpose

This project is a learning vehicle for authentication and authorization in Spring:

- Local registration and login with Bean Validation and BCrypt password hashing
- Spring Security filter chains, remember-me, and `UserDetails` wiring
- OAuth2 / OpenID Connect clients for Google and GitHub, implemented by hand (custom user services and success/failure handlers)

Patterns from this app were distilled into a reusable agent skill for scaffolding similar Spring projects (validation, Neon Postgres, Render deployment):

- [spring-project-patterns](https://github.com/Valerius-Astoria/spring-project-patterns.git)

---

## Stack

| Layer | Choice |
| --- | --- |
| Runtime | Java, Spring Boot |
| Web | Spring MVC + Thymeleaf |
| Security | Spring Security, OAuth2 Client |
| Persistence | Spring Data JPA, PostgreSQL (Neon) |
| Tests | H2 (in-memory) |
| Deploy | Docker on [Render](https://render.com) |

---

## Architecture

```
com.valerius.blog
├── controller/     # MVC endpoints (home, create, history, login, registration)
├── model/          # JPA entities (User, Blog)
├── repository/     # Spring Data repositories
├── security/       # SecurityConfig, OAuth services/handlers, registration form
└── BlogApplication.java
```

### Request flow

1. **Controllers** handle HTTP requests and return Thymeleaf views.
2. **Security** protects routes: public pages for login/registration; authenticated access for creating and viewing personal history.
3. **Form login** authenticates by email via a custom `UserDetailsService` and BCrypt.
4. **OAuth** (Google OIDC / GitHub OAuth2) maps provider profiles into local `User` records through dedicated user services and login handlers.
5. **Repositories + JPA** persist users and blog posts to Neon PostgreSQL in production.

### Security highlights

- Password encoding with `BCryptPasswordEncoder`
- Email-based account lookup
- Remember-me support (including OAuth-aware behavior)
- Provider-specific OAuth user loading (`GoogleOidcUserService`, `GithubOAuth2UserService`)
- Success and failure handlers for OAuth login outcomes

### Persistence & deployment

- **Runtime DB:** Neon PostgreSQL (`SPRING_DATASOURCE_*` env vars)
- **Tests:** H2 only
- **Hosting:** Docker web service on Render (`render.yaml`), with OAuth client secrets and remember-me key supplied as environment variables

---

## Related skill

For building or reviewing Spring Boot apps that follow the same conventions (MVC + Thymeleaf, Security, Neon, Render), use:

[https://github.com/Valerius-Astoria/spring-project-patterns.git](https://github.com/Valerius-Astoria/spring-project-patterns.git)
