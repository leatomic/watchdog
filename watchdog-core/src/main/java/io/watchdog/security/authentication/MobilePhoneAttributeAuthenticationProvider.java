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

package io.watchdog.security.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Objects;

public class MobilePhoneAttributeAuthenticationProvider extends UsernameAttributeAuthenticationProvider<MobilePhoneAttributeAuthenticationToken> {

    private MobilePhoneUserDetailsService userDetailsService;

    public MobilePhoneAttributeAuthenticationProvider(MobilePhoneUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected MobilePhoneAttributeAuthenticationToken createSuccessAuthentication(UserDetails user, Collection<? extends GrantedAuthority> authorities) {
        return new MobilePhoneAttributeAuthenticationToken(user, authorities);
    }

    @Override
    protected UserDetails loadUser(String username) throws UsernameNotFoundException {
        return userDetailsService.loadUserByMobilePhone(username);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Objects.requireNonNull(userDetailsService, "userDetailsService must be specified");
    }

}
