package io.watchdog.security.authentication;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MobilePhoneUserDetailsService {

    MobilePhoneUserDetails loadUserByMobilePhone(String mobilePhone) throws UsernameNotFoundException;;

}
