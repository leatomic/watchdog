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
