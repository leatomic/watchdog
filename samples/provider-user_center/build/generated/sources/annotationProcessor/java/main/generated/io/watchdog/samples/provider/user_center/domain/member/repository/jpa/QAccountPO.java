package io.watchdog.samples.provider.user_center.domain.member.repository.jpa;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountPO is a Querydsl query type for AccountPO
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAccountPO extends EntityPathBase<AccountPO> {

    private static final long serialVersionUID = -1629890589L;

    public static final QAccountPO accountPO = new QAccountPO("accountPO");

    public final StringPath avatar = createString("avatar");

    public final StringPath bio = createString("bio");

    public final DatePath<java.time.LocalDate> birthday = createDate("birthday", java.time.LocalDate.class);

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final DateTimePath<java.time.LocalDateTime> expirationTime = createDateTime("expirationTime", java.time.LocalDateTime.class);

    public final EnumPath<io.watchdog.samples.provider.user_center.domain.member.Gender> gender = createEnum("gender", io.watchdog.samples.provider.user_center.domain.member.Gender.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath locked = createBoolean("locked");

    public final StringPath password = createString("password");

    public final DateTimePath<java.time.LocalDateTime> passwordExpirationTime = createDateTime("passwordExpirationTime", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> passwordLastModified = createDate("passwordLastModified", java.time.LocalDate.class);

    public final StringPath phone = createString("phone");

    public final DateTimePath<java.time.LocalDateTime> registrationTime = createDateTime("registrationTime", java.time.LocalDateTime.class);

    public final StringPath username = createString("username");

    public QAccountPO(String variable) {
        super(AccountPO.class, forVariable(variable));
    }

    public QAccountPO(Path<? extends AccountPO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountPO(PathMetadata metadata) {
        super(AccountPO.class, metadata);
    }

}

