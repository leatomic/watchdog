## Watchdog

 [![LICNESE](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg)](<https://github.com/leatomic/watchdog/blob/master/LICENSE.txt>)

Watchdog（看门狗）是一个用于在 `Spring Boot` 的基础上快速构建 `用户中心` 系统的脚手架，提供 `用户中心` 系统常用功能的抽象以及默认实现，减少重复开发的成本。安全框架采用功能强大且能与 `Spring` 无缝集成的`Spring Security`。



## 快速入门

1、引入starter（有待发布到maven仓库）。

```
dependencies {
    implementation project(':watchdog-browser-starter-spring-boot')
}
```

2、在项目的外部化配置（如application.yml）中配置，启用、关闭或配置具体的功能。

例如：

```yml
watchdog:
  authentication:
    form-login.attempts-limit.enabled: true
    sms_code-login.enabled: true
```

## 模块

在Watchdog中主要包含以下3个模块:



#### watchdog-core

核心库主要提供了以下功能特性的通用抽象，这些功能特性包括：

- 认证模块

  - 表单登录：失败次数超过一定次数后的验证码拦截，失败尝试次数的限制（黑名单）
  - 短信随机码登录
  - 社交登录（待实现）：

  ​		- QQ账号登录

  ​		- 微信账号登录

  ​		- 支付宝账号登录

    - 单点登录（SSO）的整合（待实现）

- Token验证模块
  - 可配置的Token验证
  - 短信随机码验证的默认实现
  - 图片验证码验证的默认实现

- 账号注册（待实现）
  - 邮箱注册
  - 手机号码注册
  - 第三方账号注册

- 密码找回（待实现）



#### watchdog-browser

为构建针对 `Web App` 的服务提供的更多的支持，例如提供默认的登录页面（或者是前后端分离场景中的model）



#### watchdog-native

为构建针对 `Native App` 的服务提供更多的支持

