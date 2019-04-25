package io.watchdog.samples.provider.user_center.domain.member.repository.jpa;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.samples.provider.user_center.domain.member.Account.Associations;
import io.watchdog.samples.provider.user_center.domain.member.Account.Password;
import io.watchdog.samples.provider.user_center.domain.member.Account.Profile;
import io.watchdog.samples.provider.user_center.infra.repository.Translator;

public class AccountTranslator implements Translator<Account, AccountPO> {

    private final Account.Factory accountFactory;

    public AccountTranslator(Account.Factory accountFactory) {
        this.accountFactory = accountFactory;
    }

    public Account rebuild(AccountPO po) {
        return accountFactory.create(
                po.getId(), po.getUsername(),
                new Profile(po.getAvatar(), po.getBio(), po.getGender(), po.getBirthday()),
                new Associations(po.getMobilePhone(), po.getEmail()),
                new Password(po.getPassword(), po.getPasswordExpirationTime(), po.getPasswordLastModified()),
                po.isEnabled(), po.getExpirationTime(), po.isLocked(),
                po.getRegistrationTime()
        );
    }

    public <S extends Account> AccountPO persist(S account) {
        AccountPO po = new AccountPO();
        po.setId(account.getId());
        po.setUsername(account.getUsername());

        Profile profile = account.getProfile();
        po.setAvatar(profile.getAvatar());
        po.setBio(profile.getBio());
        po.setGender(profile.getGender());
        po.setBirthday(profile.getBirthday());

        Associations associations = account.getAssociations();
        po.setMobilePhone(associations.getMobilePhone());
        po.setEmail(associations.getEmail());

        Password password = account.getPassword();
        po.setPassword(password.getSeq());
        po.setPasswordExpirationTime(password.getExpirationTime());
        po.setPasswordLastModified(password.getLastModified());

        po.setLocked(account.isLocked());
        po.setEnabled(account.isEnabled());
        po.setRegistrationTime(account.getRegistrationTime());
        return po;
    }

    @Override
    public <S extends Account> S merge(AccountPO po, S account) {
        accountFactory.retread(
            account,
            po.getId(), po.getUsername(),
            new Profile(po.getAvatar(), po.getBio(), po.getGender(), po.getBirthday()),
            new Associations(po.getMobilePhone(), po.getEmail()),
            new Password(po.getPassword(), po.getPasswordExpirationTime(), po.getPasswordLastModified()),
            po.isEnabled(), po.getExpirationTime(), po.isLocked()
        );
        return account;
    }

}
