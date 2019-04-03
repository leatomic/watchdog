package io.watchdog.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

public class UsernameAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(UsernameAuthenticationProvider.class);

    private UserDetailsService userDetailsService;

    private UserDetailsChecker authenticationChecks = new DefaultAuthenticationChecks();
    private GrantedAuthoritiesMapper authoritiesMapper =  new NullAuthoritiesMapper();

    public UsernameAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Objects.requireNonNull(authentication, "authentication cannot be null");
        if (!(authentication instanceof UsernameAuthenticationToken))
            throw new IllegalArgumentException("Only UsernameAuthenticationToken is supported");

        String mobile = (String) authentication.getPrincipal();

        UserDetails user = retrieveUser(mobile);

        authenticationChecks.check(user);

        return createSuccessAuthentication(authentication, user);
    }

    private Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        UsernameAuthenticationToken result = new UsernameAuthenticationToken(
                user,
                authoritiesMapper.mapAuthorities(user.getAuthorities())
        );
        result.setDetails(authentication.getDetails());
        return result;
    }

    private UserDetails retrieveUser(String mobile) {
        try {
            UserDetails loadedUser = userDetailsService.loadUserByUsername(mobile);
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

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernameAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Objects.requireNonNull(userDetailsService, "userDetailsService must be specified");
    }

    // ~Getter Setter
    // =================================================================================================================
    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }



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
