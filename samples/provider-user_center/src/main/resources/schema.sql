-- 用户帐号表
DROP TABLE member_accounts IF EXISTS;
CREATE TABLE member_accounts (
  id BIGINT AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(32) COMMENT '用户名',
  avatar VARCHAR(255) COMMENT '头像(url)',
  bio VARCHAR(255) COMMENT '个人简介(个性签名)',
  gender ENUM('PRIVATE', 'MALE', 'FEMALE') DEFAULT 'PRIVATE' COMMENT '性别',
  birthday DATE COMMENT '生日',

  phone VARCHAR(11) COMMENT '绑定的手机号码',
  email VARCHAR(255) COMMENT '绑定的邮箱',

  password VARCHAR(72) COMMENT '登录密码',
  password_expiration_time DATETIME COMMENT '密码的过期时间，NULL表示永不过期',
  password_last_modified DATE COMMENT '上次修改密码的时间',

  is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '账号是否可用,0表示不可用(默认，需要激活)，1表示可用',
  expiration_time DATETIME COMMENT '账号的过期时间，NULL表示永不过期',
  is_locked  TINYINT NOT NULL DEFAULT 0 COMMENT '账号是否未被冻结,0表示未被冻结（默认），1表示已被冻结',

  registration_time DATETIME NOT NULL COMMENT '注册时间',

  PRIMARY KEY (id),
  UNIQUE KEY uk_username(username),
  UNIQUE KEY uk_phone(phone),
  UNIQUE KEY uk_email(email)
);


-- Spring Social中的连接表，存储每一个第三方账号的关联信息
drop table member_UserConnection if exists;
create table member_UserConnection (
  userId varchar(255) not null,
  providerId varchar(255) not null,
  providerUserId varchar(255),
  rank int not null,
  displayName varchar(255),
  profileUrl varchar(512),
  imageUrl varchar(512),
  accessToken varchar(512) not null,
  secret varchar(512),
  refreshToken varchar(512),
  expireTime bigint,
  primary key (userId, providerId, providerUserId)
);
create unique index UserConnectionRank on member_UserConnection(userId, providerId, rank);
