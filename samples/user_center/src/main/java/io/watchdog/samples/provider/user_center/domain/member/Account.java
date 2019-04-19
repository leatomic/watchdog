package io.watchdog.samples.provider.user_center.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.watchdog.samples.provider.user_center.infrastructure.security.authentication.CredentialsExpirationPolicy;
import io.watchdog.util.Durations;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
    @JsonIgnore
    private Password password;

    private LocalDateTime expirationTime;
    private boolean locked;
    private boolean enabled = true;
    private final LocalDateTime registrationTime;

    @JsonIgnore
    private final transient Factory factory;

    // ~Constructor for create new one
    Account(String username, Factory factory) {
        this.id = null;
        this.username = username;
        this.registrationTime   = LocalDateTime.now();
        this.factory = factory;
    }

    // ~Constructor for rebuild of persisted
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
        this.associations.setPhone(phone);
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

