package io.watchdog.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Objects;

public abstract class UsernameAttributeAuthenticationProvider<T extends UsernameAttributeAuthenticationToken>
        implements AuthenticationProvider, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(UsernameAttributeAuthenticationProvider.class);

    @SuppressWarnings("unchecked")
    private Class<T> tokenClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    private UserDetailsChecker authenticationChecks = new DefaultAuthenticationChecks();
    private GrantedAuthoritiesMapper authoritiesMapper =  new NullAuthoritiesMapper();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Objects.requireNonNull(authentication, "authentication cannot be null");
        if (!supports(authentication.getClass()))
            throw new IllegalArgumentException("Only " + tokenClass.getName() + " is supported");

        String username = (String) authentication.getPrincipal();

        UserDetails user = retrieveUser(username);

        authenticationChecks.check(user);

        return createSuccessAuthentication(authentication, user);
    }

    private Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        T result = createSuccessAuthentication(user, authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());
        return result;
    }

    protected abstract T createSuccessAuthentication(UserDetails user, Collection<? extends GrantedAuthority> authorities);

    private UserDetails retrieveUser(String username) {
        try {
            UserDetails loadedUser = loadUser(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation"
                );
            }
            return loadedUser;
        }
        catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    protected abstract UserDetails loadUser(String username) throws UsernameNotFoundException;

    @Override
    public boolean supports(Class<?> authentication) {
        return tokenClass.isAssignableFrom(authentication);
    }

    // ~Getter Setter
    // =================================================================================================================

    private class DefaultAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                logger.debug("User account is locked");
                throw new LockedException("User account is locked");
            }

            if (!user.isEnabled()) {
                logger.debug("User account is disabled");
                throw new DisabledException("User is disabled");
            }

            if (!user.isAccountNonExpired()) {
                logger.debug("User account is expired");
                throw new AccountExpiredException("User account has expired");
            }

            if (!user.isCredentialsNonExpired()) {
                logger.debug("User account credentials have expired");
                throw new CredentialsExpiredException("User credentials have expired");
            }
        }
    }
}
