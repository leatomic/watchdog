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

package io.watchdog.samples.provider.user_center.infra.security.authentication;

import io.watchdog.samples.provider.user_center.service.AccountService;
import io.watchdog.security.authentication.MobilePhoneUserDetails;
import io.watchdog.security.authentication.MobilePhoneUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class DaoUserDetailsService implements UserDetailsService, MobilePhoneUserDetailsService {

    private final AccountService accountService;

    public DaoUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (log.isDebugEnabled()) {
            log.debug("attempt to load user by username: " + username);
        }

        return accountService.findByUsername(username)
                .map(AccountUserDetailsAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("username: " + username + " not found"));
    }


    @Override
    public MobilePhoneUserDetails loadUserByMobilePhone(String mobilePhone) throws UsernameNotFoundException {
        if (log.isDebugEnabled()) {
            log.debug("attempt to load user by mobile mobilePhone: " + mobilePhone);
        }

        return accountService.findByMobilePhone(mobilePhone)
                .map(AccountMobilePhoneUserDetailsAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("user for mobile mobilePhone: " + mobilePhone + " not found"));
    }
}

