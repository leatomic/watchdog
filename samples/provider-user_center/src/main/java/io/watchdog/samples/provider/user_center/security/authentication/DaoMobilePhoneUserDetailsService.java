package io.watchdog.samples.provider.user_center.security.authentication;

import io.watchdog.samples.provider.user_center.service.AccountService;
import io.watchdog.security.authentication.MobilePhoneUserDetails;
import io.watchdog.security.authentication.MobilePhoneUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class DaoMobilePhoneUserDetailsService implements MobilePhoneUserDetailsService {

    private final AccountService accountService;

    public DaoMobilePhoneUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public MobilePhoneUserDetails loadUserByMobilePhone(String mobilePhone) throws UsernameNotFoundException {

        if (log.isDebugEnabled()) {
            log.debug("attempt to load user by mobile phone: " + mobilePhone);
        }

        return accountService.findByMobilePhone(mobilePhone)
                .map(AccountMobilePhoneDetaisAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("user for mobile phone: " + mobilePhone + " not found"));
    }
}
