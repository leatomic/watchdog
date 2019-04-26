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
import io.watchdog.samples.provider.user_center.domain.member.repository.AccountRepository;
import io.watchdog.samples.provider.user_center.infra.repository.PersistentObjectRepository;
import io.watchdog.samples.provider.user_center.infra.repository.Translator;

import javax.annotation.Nonnull;


public class AccountJpaRepository implements AccountRepository<AccountPO> {

    private final AccountPORepository delegate;
    private final AccountTranslator translator;

    public AccountJpaRepository(AccountPORepository delegate, Account.Factory userFactory) {
        this.delegate = delegate;
        this.translator = new AccountTranslator(userFactory);
    }

    @Override @Nonnull
    public PersistentObjectRepository<AccountPO, Long> delegate() {
        return delegate;
    }

    @Override @Nonnull
    public Translator<Account, AccountPO> translator() {
        return translator;
    }

}
