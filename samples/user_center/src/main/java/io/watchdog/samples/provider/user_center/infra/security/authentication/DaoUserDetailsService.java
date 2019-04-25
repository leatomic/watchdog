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

