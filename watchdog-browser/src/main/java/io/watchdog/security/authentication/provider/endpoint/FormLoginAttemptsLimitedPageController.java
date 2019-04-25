package io.watchdog.security.authentication.provider.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class FormLoginAttemptsLimitedPageController {

    @GetMapping("${watchdog.authentication.form-login.attempts-limit.login-disabled-url:/form-login.disabled}")
    @ResponseBody
    public ResponseEntity attemptFailure() {
        return ResponseEntity.ok("You've tried form login too many times");
    }

}
