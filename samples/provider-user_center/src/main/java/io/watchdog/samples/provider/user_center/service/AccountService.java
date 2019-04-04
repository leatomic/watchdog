package io.watchdog.samples.provider.user_center.service;

import io.watchdog.samples.provider.user_center.domain.member.Account;

import java.time.Duration;
import java.util.Optional;

public interface AccountService {

    Account create(AccountCreation cmd);

    //---------------------------------------------------------------------
    // '查找用户对象'部分的接口，业务功能仅开放对单个用户的查询
    // 集合操作的接口可直接调用Spring Data Rest Mvc接口
    //---------------------------------------------------------------------
    /**
     * 根据用户ID查找（未被软删除）用户对象
     * @return 若传入的id对应的用户存在且未被软删除则返回装有该对象的Optional集合，
     *         否则返回Optional.EMPTY
     */
    Optional<Account> get(Long id);

    /**
     * 根据用户ID查找（未被软删除）用户对象
     */
    Account load(Long id);

    /**
     * 根据用户名查找（未被软删除）用户对象
     * @return 若传入的用户对应的用户存在且未被软删除则返回装有该对象的Optional集合，
     *         否则返回Optional.EMPTY
     */
    Optional<Account> findByUsername(String username);


    Optional<Account> findByMobilePhone(String mobile);

    /**
     * 检测手机号码是否可用（于注册）
     * @return 若该手机号码已绑定到某一用户（无论该用户是否已注销）则返回false，否则返回true
     */
    boolean detectPhone(String phone);

    /**
     * 检测是否邮箱已被使用
     * @return 若已被使用则返回true，否则返回false
     */
    boolean detectEmail(String email);



    //---------------------------------------------------------------------
    // '操作用户对象'部分的接口
    //---------------------------------------------------------------------
    void lock(Account user);

    void unlock(Account user);

    void enable(Account user);

    void disable(Account user);

    void renew(Account user, Duration validity);

    void resetPassword(Account user, String oldPassword, String newPassword);

    void cancel(Account user);

    void update(Account user);

}
