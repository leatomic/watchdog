package io.watchdog.samples.sms_code_login.infra.security.authentication;

import io.watchdog.security.authentication.MobilePhoneUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MobilePhoneUserDetailsAdapter extends User implements MobilePhoneUserDetails {

    public MobilePhoneUserDetailsAdapter(String mobilePhone, Collection<? extends GrantedAuthority> authorities) {
        super(mobilePhone, "null", authorities);
    }

    @Override
    public String getMobilePhone() {
        return getUsername();
    }
}
