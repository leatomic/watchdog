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
