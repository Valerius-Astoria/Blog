package com.valerius.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Blog Spring Boot application.
 * <p>
 * Enables component scanning and auto-configuration for the
 * {@code com.valerius.blog} base package and its subpackages.
 *
 * @author Valerius
 */
@SpringBootApplication
public class BlogApplication {

    /**
     * Launches the application.
     *
     * @param args command-line arguments passed to the Spring
     *             application context; must not be {@code null}
     */
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

}
