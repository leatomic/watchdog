package io.watchdog.samples.provider.user_center.infrastructure.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PersistentObjectRepository<P, PK>
        extends PagingAndSortingRepository<P, PK>, QuerydslPredicateExecutor<P> {
}
