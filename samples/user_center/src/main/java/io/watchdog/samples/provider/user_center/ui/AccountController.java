/*
 * Copyright (c) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.watchdog.samples.provider.user_center.ui;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.samples.provider.user_center.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class AccountController {

    // =================================================================================================================

    private AccountService service;
//    private final AuthenticationManager authenticationManager;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Autowired
    public AccountController(AccountService service
//            , AuthenticationManager authenticationManager
    ) {
        this.service = service;
//        this.authenticationManager = authenticationManager;
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

//    @GetMapping("/sign-up/with-mobilePhone/mobilePhone.unused")
//    @ResponseBody
//    public ResponseEntity<?> checkPhoneUnused(@RequestParam("mobilePhone") @Valid @MobilePhone String mobilePhone) {
//        boolean unused = !service.detectPhone(mobilePhone);
//        SimpleResponseBody result = unused
//                ? new SimpleResponseBody("mobilePhone.unused", null, null)
//                : new SimpleResponseBody("duplicated-mobilePhone", "该手机号码已被用于注册，无法用于注册新帐号", null);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/sign-up/with-mobilePhone.do")
//    @ResponseBody
//    public ResponseEntity<?> doSignUpAfterSmsCodeVerified(HttpServletRequest request) {
//
//        String mobilePhone = (String) request.getAttribute(WebAttributes.PHONE_OF_SMS_CODE_VERIFIED);
//
//        if (log.isDebugEnabled()) {
//            log.debug("try to sign up with mobilePhone: " + mobilePhone);
//        }
//
//        service.create(new AccountCreation.WithPhone(mobilePhone));
//
//        doSignInAfterSignUpSucceed(new MobilePhoneAttributeAuthenticationToken(mobilePhone), request);
//
//        return ResponseEntity.created(URI.create("/members/me")).build();
//
//    }
//
//    private void doSignInAfterSignUpSucceed(AbstractAuthenticationToken token, HttpServletRequest request) {
//
//        Object details = authenticationDetailsSource.buildDetails(request);
//        token.setDetails(details);
//
//        Authentication authenticated = authenticationManager.authenticate(token);
//
//        SecurityContextHolder.getContext().setAuthentication(authenticated);
//
//    }



}
