/*
 * Copyright (c) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
