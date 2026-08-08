package com.valerius.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Valerius Blog application.
 *
 * @author Valerius
 */
@SpringBootApplication
public class BlogApplication {

    /**
     * Boots the application context.
     *
     * @param args command-line arguments; must not be {@code null}
     */
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

}
