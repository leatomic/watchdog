package io.watchdog.samples.provider.user_center.domain.member.repository;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.samples.provider.user_center.infrastructure.repository.EntityRepository;

public interface AccountRepository<P> extends EntityRepository<Account, P, Long> {

}