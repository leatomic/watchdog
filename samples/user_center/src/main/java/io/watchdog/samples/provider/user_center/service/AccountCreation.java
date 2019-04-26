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

package io.watchdog.samples.provider.user_center.service;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public abstract class AccountCreation {

    protected Account.Factory executor;

    public AccountCreation delegateTo(Account.Factory executor) {
        this.executor = Objects.requireNonNull(executor);
        return this;
    }

    public abstract Account execute();

    public static class WithEmail extends AccountCreation {
        private String email;
        private String password;

        public WithEmail(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public Account execute() {
            Account account = executor.create();
            account.bindEmail(email);
            account.setPassword(password);
            return account;
        }
    }

    @Getter @Setter @AllArgsConstructor
    public static class WithPhone extends AccountCreation {
        private String phone;
        @Override
        public Account execute() {
            Account user = executor.create();
            user.bindPhone(phone);
            return user;
        }
    }
}
