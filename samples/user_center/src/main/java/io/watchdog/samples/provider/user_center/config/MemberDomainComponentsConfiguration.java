package io.watchdog.samples.provider.user_center.config;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.samples.provider.user_center.domain.member.repository.AccountRepository;
import io.watchdog.samples.provider.user_center.domain.member.repository.jpa.AccountJpaRepository;
import io.watchdog.samples.provider.user_center.domain.member.repository.jpa.AccountPORepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class MemberDomainComponentsConfiguration {

    @Bean
    public Account.Factory accountFactory(PasswordEncoder passwordEncoder) {
        return new Account.Factory(passwordEncoder);
    }

    @Bean
    protected AccountRepository accountRepository(AccountPORepository delegate, Account.Factory accountFactory) {
        return new AccountJpaRepository(delegate, accountFactory);
    }

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }

}
