package io.watchdog.security.authentication;

import org.springframework.security.core.userdetails.UserDetails;

public interface MobilePhoneUserDetails extends UserDetails {
    String getMobilePhone();
}
