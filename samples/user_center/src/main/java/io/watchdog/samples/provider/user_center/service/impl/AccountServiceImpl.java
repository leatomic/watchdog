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

package io.watchdog.samples.provider.user_center.service.impl;

import com.querydsl.core.types.Predicate;
import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.samples.provider.user_center.domain.member.repository.AccountRepository;
import io.watchdog.samples.provider.user_center.domain.member.repository.jpa.AccountPO;
import io.watchdog.samples.provider.user_center.service.AccountCreation;
import io.watchdog.samples.provider.user_center.service.AccountService;
import io.watchdog.samples.provider.user_center.service.UserNotFoundOrCanceledException;
import io.watchdog.validation.MobilePhoneValidator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

import static io.watchdog.samples.provider.user_center.domain.member.repository.jpa.QAccountPO.accountPO;

/**
 * 默认的操作用户账号服务
 * @see AccountService
 * @author le
 * @since v0.1.0
 */
@Service
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    public AccountServiceImpl(@NonNull Account.Factory userFactory, @NonNull AccountRepository<AccountPO> accountRepository,
                              @NonNull EmailValidator emailValidator, @NonNull MobilePhoneValidator mobilePhoneValidator) {
        this.userFactory        = userFactory;
        this.accountRepository  = accountRepository;
        this.emailValidator         = emailValidator;
        this.mobilePhoneValidator   = mobilePhoneValidator;
    }


    private Account.Factory userFactory;
    private AccountRepository<AccountPO> accountRepository;

    @Override
    public Account create(AccountCreation cmd) {
        Account user = cmd.delegateTo(userFactory).execute();
        return accountRepository.save(user);
    }

    @Override
    public Optional<Account> get(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Account load(Long id) {
        return accountRepository.findById(id).orElseThrow(()->new UserNotFoundOrCanceledException(id));
    }

    private EmailValidator emailValidator;
    private MobilePhoneValidator mobilePhoneValidator;

    @Override
    public Optional<Account> findByUsername(String username) {

        if (log.isDebugEnabled()) {
            log.debug("find account with username:{}", username);
        }

        Predicate who;

        if (emailValidator.isValid(username, null)) {
            who = accountPO.email.eq(username);
        }
        else if (mobilePhoneValidator.isValid(username, null)) {
            who = accountPO.mobilePhone.eq(username);
        }
        else who = accountPO.username.eq(username);

        return accountRepository.findOne(who);
    }

    @Override
    public Optional<Account> findByMobilePhone(String mobile) {
        if (log.isDebugEnabled()) {
            log.debug("find account with mobile mobilePhone:{}", mobile);
        }
        return accountRepository.findOne(accountPO.mobilePhone.eq(mobile));
    }

    @Override
    public boolean detectEmail(String email) {
        return accountRepository.exists(accountPO.email.eq(email));
    }

    @Override
    public boolean detectPhone(String phone) {
        return accountRepository.exists(accountPO.mobilePhone.eq(phone));
    }

    @Override
    public void lock(Account user) {
        user.lock();
        accountRepository.save(user);
    }

    @Override
    public void unlock(Account user) {
        user.unLock();
        accountRepository.save(user);
    }

    @Override
    public void enable(Account user) {
        user.enable();
        accountRepository.save(user);
    }

    @Override
    public void disable(Account user) {
        user.disable();
        accountRepository.save(user);
    }

    @Override
    public void renew(Account user, Duration validity) {
        user.renew(validity);
        accountRepository.save(user);
    }

    @Override
    public void resetPassword(Account user, String oldPassword, String newPassword) {
        user.resetPassword(oldPassword, newPassword);
        accountRepository.save(user);
    }

    @Override
    public void cancel(Account user) {
        user.enable();
        accountRepository.save(user);
    }

    @Override
    public void update(Account user) {
        accountRepository.save(user);
    }

}
