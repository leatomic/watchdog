package io.watchdog.samples.provider.user_center.domain.member.repository.jpa;

import io.watchdog.samples.provider.user_center.domain.member.Account;
import io.watchdog.validation.MobilePhone;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 实体'账户'的映射类
 * @author le
 */
@Entity
@Table(
    name = "member_accounts",
    uniqueConstraints = {
        @UniqueConstraint(name = AccountPO.ConstraintNames.UK_USERNAME,         columnNames = "username"),
        @UniqueConstraint(name = AccountPO.ConstraintNames.UK_MOBILE_PHONE,     columnNames = "mobilePhone"),
        @UniqueConstraint(name = AccountPO.ConstraintNames.UK_EMAIL,            columnNames = "email")
    }
)
@Getter @Setter @ToString
public class AccountPO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String avatar;
    @Column(nullable = false)
    private String bio;
    @Column(nullable = false)
    private Account.Gender gender = Account.Gender.PRIVATE;
    @Column @Past
    private LocalDate birthday;

    @Column @MobilePhone
    private String mobilePhone;
    @Column @Email
    private String email;

    @Column(length = 72)
    private String password;
    @Column
    private LocalDateTime passwordExpirationTime;
    @Column
    private LocalDate passwordLastModified;

    @Column(name = "is_enabled")
    private boolean enabled = true;
    @Column
    private LocalDateTime expirationTime;
    @Column(name = "is_locked")
    private boolean locked;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountPO user = (AccountPO) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public static class ConstraintNames {
        public static final String UK_USERNAME      = "uk_username";
        public static final String UK_MOBILE_PHONE  = "uk_mobile_phone";
        public static final String UK_EMAIL         = "uk_email";
    }

}
