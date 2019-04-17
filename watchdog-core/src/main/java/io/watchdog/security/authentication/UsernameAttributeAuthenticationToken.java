package io.watchdog.security.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;
import java.util.Objects;

public abstract class UsernameAttributeAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    public UsernameAttributeAuthenticationToken(String usernameAttr) {
        super(null);
        if (StringUtils.isBlank(usernameAttr)) {
            throw new IllegalArgumentException("username attribute cannot be blank");
        }
        this.principal = usernameAttr;
        setAuthenticated(false);
    }

    public UsernameAttributeAuthenticationToken(Object principal,
                                                Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = Objects.requireNonNull(principal, "principal cannot be null");
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {

        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot save this token to trusted - use constructor which takes a GrantedAuthority list instead"
            );
        }

        super.setAuthenticated(false);
    }
}
