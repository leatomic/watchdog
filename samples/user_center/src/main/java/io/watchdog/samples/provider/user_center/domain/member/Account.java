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

package io.watchdog.samples.provider.user_center.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.watchdog.samples.provider.user_center.infra.security.authentication.CredentialsExpirationPolicy;
import io.watchdog.util.Durations;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @ToString
public class Account {

    private Long id;
    private String username;
    private Profile profile;
    private Associations associations;
    private @JsonIgnore Password password;

    private LocalDateTime expirationTime;
    private boolean locked;
    private boolean enabled = true;
    private final LocalDateTime registrationTime;

    private final transient @JsonIgnore Factory factory;

    // ~Constructor for create new one
    Account(String username, Factory factory) {
        this.id = null;
        this.username = username;
        this.registrationTime   = LocalDateTime.now();
        this.factory = factory;
    }

    // ~Constructor for rebuild from persistent data
    Account(Long id, String username,
            Profile profile,
            Associations associations,
            Password password,
            boolean enabled, LocalDateTime expirationTime, boolean locked, LocalDateTime registrationTime,
            Factory factory) {
        this.id = id;
        this.username = username;
        this.profile = profile;
        this.associations = associations;
        this.password = password;
        this.expirationTime = expirationTime;
        this.enabled = enabled;
        this.locked = locked;
        this.registrationTime = registrationTime;
        this.factory = factory;
    }


    // ~基本信息的操作
    // ==================================================================================
    public void updateProfile(Profile profile) {
        confirmEnabled();
        confirmNotExpired();
        confirmNotLocked();
        this.profile = profile;
    }



    // ~关联绑定信息的操作
    // ======================================================================
    public void bindPhone(String phone) {
        confirmEnabled();
        confirmNotExpired();
        confirmNotLocked();
        this.associations.setMobilePhone(phone);
    }

    public void bindEmail(String email) {
        confirmEnabled();
        confirmNotExpired();
        confirmNotLocked();
        this.associations.setEmail(email);
    }





    //~ 登录账号相关的操作
    //====================================================================================
    public void setPassword(String password) {
        confirmEnabled();
        confirmNotExpired();
        confirmNotLocked();

        PasswordEncoder passwordEncoder = factory.getPasswordEncoder();

        this.password = new Password(
                passwordEncoder.encode(password),
                factory.getPasswordExpirationPolicy().newExpirationTimeForCredentialsReset(),
                LocalDate.now()
        );
    }

    public void resetPassword(String oldPassword, String newPassword) {
        confirmEnabled();
        confirmNotExpired();
        confirmNotLocked();

        PasswordEncoder passwordEncoder = factory.getPasswordEncoder();
        if (!passwordEncoder.matches(oldPassword, password.getSeq())) {
            throw new UnsupportedOperationException("wrong old password");
        }

        this.password = new Password(
                passwordEncoder.encode(newPassword),
                factory.getPasswordExpirationPolicy().newExpirationTimeForCredentialsReset(),
                LocalDate.now()
        );
    }

    public boolean isCredentialsExpired() {
        return password.isExpired();
    }



    public void renew(Duration validity) {

        Durations.requiresNullOrPositive(validity, "validity");

        confirmEnabled();

        if (expirationTime == null) {
            // 该账号的有效期已经是永久的，无须继续延长其有效期
            return;
        }

        if (validity == null) {
            expirationTime = null;
        }
        else {
            LocalDateTime   now     = LocalDateTime.now();
            boolean isExpired = expirationTime.isBefore(now);
            LocalDateTime  base     = isExpired ? now : expirationTime;
            expirationTime          = base.plus(validity);
        }
    }

    public boolean isExpired() {
        return expirationTime != null && expirationTime.isBefore(LocalDateTime.now());
    }

    public void lock() {
        confirmEnabled();
        confirmNotExpired();

        if (isLocked())
            return;

        this.locked = true;
    }

    public void unLock() {
        confirmEnabled();
        confirmNotExpired();

        if (!isLocked())
            return;

        this.locked = false;
    }

    /**
     * 禁用该账号。这通常是由管理员操作，或者触发某些条件(例如发生违规操作)而自动发生的
     */
    public void disable() {
        if (!isEnabled())
            return;

        this.enabled = false;
    }

    /**
     * 启用/恢复账号。这通常是由管理员操作，或者满足某些条件而自动发生的
     */
    public void enable() {
        if (isEnabled())
            return;

        this.enabled = true;
    }

    private void confirmEnabled() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("user account is disabled");
        }
    }

    private void confirmNotLocked() {
        if (isLocked()) {
            throw new UnsupportedOperationException("user account is locked");
        }
    }

    private void confirmNotExpired() {
        if (isExpired()) {
            throw new UnsupportedOperationException("user account is expired");
        }
    }



    @Data @AllArgsConstructor
    public static class Profile {

        private String avatar;      // 头像（图片的url）
        private String bio;         // 个人简介
        private Gender gender;      // 性别，默认是为公开的
        private LocalDate birthday; // 生日
    }

    public enum Gender {
        PRIVATE,             // 未公开的
        MALE,                // 男性的
        FEMALE;              // 女性的
        // ...其他的
    }

    @Data @AllArgsConstructor
    public static class Password {

        private String seq;                         //  密码串
        private LocalDateTime expirationTime;       //  密码到期时间，为null表示永不过期
        private LocalDate lastModified;             //  上次修改的时间，默认为密码初始化时间
        public boolean isExpired() {
            return expirationTime != null && expirationTime.isBefore(LocalDateTime.now());
        }
    }

    @Data @AllArgsConstructor
    public static class Associations {
        private String mobilePhone; // 绑定的手机号码
        private String email;       // 绑定的邮箱
    }


    /**
     * <p>Account实体对象的工厂类
     * <br>
     * <br>
     *
     * <p>主要职责：
     * <ul>
     *  <li>创建新的Account对象</li>
     *  <li>根据从仓储中取得的持久化数据来重建Account对象</li>
     * </ul>
     * <p/>
     */
    @Getter @Setter
    public static class Factory {

        private UserNameGenerator usernameGenerator;
        private PasswordEncoder passwordEncoder;
        private CredentialsExpirationPolicy passwordExpirationPolicy = new CredentialsExpirationPolicy.Easy();

        //~Constructs
        //=============================================================================================
        public Factory(PasswordEncoder passwordEncoder) {
            this.usernameGenerator = () -> "user-" + UUID.randomUUID().toString();
            this.passwordEncoder = passwordEncoder;
        }

        public Factory(UserNameGenerator usernameGenerator, PasswordEncoder passwordEncoder) {
            this(passwordEncoder);
            this.usernameGenerator = usernameGenerator;
        }

        //~Factory methods
        //=============================================================================================
        public Account create() {
            String username = usernameGenerator.generate();
            return new Account(username,this);
        }

        public Account create(
                Long id, String username,
                Profile profile,
                Associations associations,
                Password password,
                boolean enabled, LocalDateTime expirationTime, boolean locked, LocalDateTime registrationTime) {

            return new Account(
                    id, username,
                    profile,
                    associations,
                    password,
                    enabled, expirationTime, locked, registrationTime,
                    this
            );

        }

        /**
         * 类似于merge(),主要是在save或者是update某个Account对象时，需要确保操作前的和返回的是同一个对象，
         * 因此我们可以将数据库操作后的数据合并到操作前的对象上
         */
        public void retread(Account target,
                            Long id, String username,
                            Profile profile,
                            Associations associations,
                            Password password,
                            boolean enabled, LocalDateTime expirationTime, boolean locked) {

            target.id = id;
            target.username = username;
            target.profile = profile;
            target.associations = associations;
            target.password = password;

            target.enabled = enabled;
            target.expirationTime = expirationTime;
            target.locked = locked;
        }

        /**
         * <strong>用户名生成策略</strong><br/><br/>
         *
         * <p>为了让用户更方便快捷的完成注册，我们在默认为用户生成名称(昵称，用来@的全局唯一的)</p>
         *
         * <p>为什么默认生成？
         *      因为与邮箱、手机号这些相比，昵称的命名很容易冲突，很容易造成注册的不顺畅，从而导致用户容易放弃当前的注册操作<p>
         *
         * <p>为了确保我们顺利向数据库表插入带有默认昵称的记录，我们生成的昵称应与允许用户输入的不冲突
         * 我们可以在生成默认昵称的时候放宽限制，而对用户自己修改的时候加强限制</p>
         */
        public interface UserNameGenerator {

            String generate();

        }
    }
}

