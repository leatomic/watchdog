package io.watchdog.samples.form_login_attempt_limit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    public String root() {
        return "hello world";
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> current(Authentication authentication) {

        Object principal;

        if (authentication == null || (principal = authentication.getPrincipal()) == null) {
            throw new IllegalStateException("must be authenticated");
        }

        return ResponseEntity.ok(principal);
    }

}

