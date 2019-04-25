package io.watchdog.samples.provider.user_center.infra.security.authentication;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.security.authentication.MobilePhoneUserDetails;

public class AccountMobilePhoneUserDetailsAdapter extends AccountUserDetailsAdapter implements MobilePhoneUserDetails {

    public AccountMobilePhoneUserDetailsAdapter(Account target) {
        super(target);
    }

    @Override
    public String getMobilePhone() {
        return getTarget().getAssociations().getMobilePhone();
    }

}
