package io.watchdog.samples.provider.user_center.security.authentication;

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

    public Account getTarget() {
        return target;
    }

}
