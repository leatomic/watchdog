package io.watchdog.samples.sms_code_login.infra.security.authentication;

import io.watchdog.security.authentication.MobilePhoneUserDetails;
import io.watchdog.security.authentication.MobilePhoneUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryMobilePhoneUserDetailsManager implements MobilePhoneUserDetailsService {

    private Map<String, MobilePhoneUserDetails> users = new HashMap<>(16);

    public InMemoryMobilePhoneUserDetailsManager withUsers(MobilePhoneUserDetails... users) {
        for (MobilePhoneUserDetails user: Objects.requireNonNull(users)) {
            Objects.requireNonNull(user);
            this.users.put(user.getMobilePhone(), user);
        }
        return this;
    }

    @Override
    public MobilePhoneUserDetails loadUserByMobilePhone(String mobilePhone) throws UsernameNotFoundException {
        MobilePhoneUserDetails target = users.get(mobilePhone);
        if (target == null)
            throw new UsernameNotFoundException("MobilePhoneUserDetails for mobile phone: " + mobilePhone);
        return target;
    }

}
