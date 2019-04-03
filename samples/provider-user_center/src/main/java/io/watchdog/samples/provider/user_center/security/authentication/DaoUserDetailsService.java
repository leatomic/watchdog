package io.watchdog.samples.provider.user_center.security.authentication;

import io.watchdog.samples.provider.user_center.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class DaoUserDetailsService implements UserDetailsService {

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


}

