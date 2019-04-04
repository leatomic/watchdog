package io.watchdog.samples.provider.user_center.ui;

import io.watchdog.http.SimpleResponseBody;
import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.samples.provider.user_center.service.AccountCreation;
import io.watchdog.samples.provider.user_center.service.AccountService;
import io.watchdog.security.authentication.MobilePhoneAuthenticationToken;
import io.watchdog.security.web.WebAttributes;
import io.watchdog.validation.MobilePhone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@Slf4j
@Controller
public class AccountController {

    // =================================================================================================================

    private AccountService service;
    private final AuthenticationManager authenticationManager;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Autowired
    public AccountController(AccountService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/members/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> current(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("must be authenticated");
        }

        String username = ((UserDetails)authentication.getPrincipal()).getUsername();

        Account account = service.findByUsername(username).orElseThrow(()-> {
            String message = "account dose not existed. it seems to be canceled, current user supposed to been logged out";
            return new IllegalStateException(message);
        });

        return ResponseEntity.ok(account);
    }

    @GetMapping("/sign-up/with-phone/phone.unused")
    @ResponseBody
    public ResponseEntity<?> checkPhoneUnused(@RequestParam("phone") @Valid @MobilePhone String phone) {
        boolean unused = !service.detectPhone(phone);
        SimpleResponseBody result = unused
                ? new SimpleResponseBody("phone.unused", null, null)
                : new SimpleResponseBody("duplicated-phone", "该手机号码已被用于注册，无法用于注册新帐号", null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-up/with-phone.do")
    @ResponseBody
    public ResponseEntity<?> doSignUpAfterSmsCodeVerified(HttpServletRequest request) {

        String phone = (String) request.getAttribute(WebAttributes.PHONE_OF_SMS_CODE_VERIFIED);

        if (log.isDebugEnabled()) {
            log.debug("try to sign up with phone: " + phone);
        }

        service.create(new AccountCreation.WithPhone(phone));

        doSignInAfterSignUpSucceed(new MobilePhoneAuthenticationToken(phone), request);

        return ResponseEntity.created(URI.create("/members/me")).build();

    }

    private void doSignInAfterSignUpSucceed(AbstractAuthenticationToken token, HttpServletRequest request) {

        Object details = authenticationDetailsSource.buildDetails(request);
        token.setDetails(details);

        Authentication authenticated = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticated);

    }



}
