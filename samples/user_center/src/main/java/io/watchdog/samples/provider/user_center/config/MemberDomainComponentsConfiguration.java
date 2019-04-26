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
