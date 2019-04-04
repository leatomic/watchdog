package io.watchdog.samples.provider.user_center.security.authentication;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.security.authentication.MobilePhoneUserDetails;

public class AccountMobilePhoneDetaisAdapter extends AccountUserDetailsAdapter implements MobilePhoneUserDetails {

    public AccountMobilePhoneDetaisAdapter(Account target) {
        super(target);
    }

    @Override
    public String getMobilePhone() {
        return getTarget().getAssociations().getPhone();
    }

}
