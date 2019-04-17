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
