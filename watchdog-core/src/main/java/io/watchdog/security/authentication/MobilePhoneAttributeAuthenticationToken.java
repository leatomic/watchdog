package io.watchdog.security.authentication;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MobilePhoneAttributeAuthenticationToken extends UsernameAttributeAuthenticationToken {

    public MobilePhoneAttributeAuthenticationToken(String usernameAttr) {
        super(usernameAttr);
    }

    public MobilePhoneAttributeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(principal, authorities);
    }

}
