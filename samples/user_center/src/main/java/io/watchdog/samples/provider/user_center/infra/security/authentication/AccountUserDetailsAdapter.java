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

import io.watchdog.samples.provider.user_center.domain.member.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class AccountUserDetailsAdapter implements UserDetails {

    private final Account target;

    public AccountUserDetailsAdapter(Account target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
    }

    @Override
    public String getPassword() {
        return target.getPassword().getSeq();
    }

    @Override
    public String getUsername() {
        return target.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !target.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !target.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !target.isCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return target.isEnabled();
    }

    protected Account getTarget() {
        return target;
    }

}
