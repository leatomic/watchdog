package io.watchdog.samples.provider.user_center.infra.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * <p>
 *     领域对象（不是持久化实体）的仓储接口,
 *     该接口参考 {@link PagingAndSortingRepository}，以及{@link QuerydslPredicateExecutor}来设计。
 * <p>
 *     内部使用{@link PersistentObjectRepository}来与数据库交互，
 *     再借由{@link Translator}接口来实现领域对象和持久化映射对象之间的转化
 * <p/>
 * @param <E> 领域对象的类型
 * @param <P> 持久化实体的类型
 * @param <PK> 持久化实体主键的类型（也是领域对象主键的类型）
 * @see PagingAndSortingRepository
 * @see QuerydslPredicateExecutor
 */
public interface EntityRepository<E, P, PK> {

    @NonNull
    PersistentObjectRepository<P, PK> delegate();

    @NonNull
    Translator<E, P> translator();


    default Optional<E> findOne(@NonNull Predicate predicate) {
        return delegate().findOne(predicate)
                            .map(translator()::rebuild);
    }

    default Iterable<E> findAll(@NonNull Predicate predicate) {
        return translator().rebuildAll(
                                delegate().findAll(predicate)
                            );
    }

    default Iterable<E> findAll(@NonNull Predicate predicate, @NonNull Sort sort) {
        return translator().rebuildAll(
                                delegate().findAll(predicate, sort)
                            );
    }

    default Iterable<E> findAll(@NonNull Predicate predicate, @NonNull OrderSpecifier<?>... orders) {
        return translator().rebuildAll(
                                delegate().findAll(predicate, orders)
                            );
    }

    default Iterable<E> findAll(@NonNull OrderSpecifier<?>... orders) {
        return translator().rebuildAll(
                                delegate().findAll(orders)
                            );
    }

    default Page<E> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return delegate().findAll(predicate, pageable)
                            .map(translator()::rebuild);
    }

    default long count(@NonNull Predicate predicate) {
        return delegate().count(predicate);
    }

    default boolean exists(@NonNull Predicate predicate) {
        return delegate().exists(predicate);
    }

    default Iterable<E> findAll(@NonNull Sort sort) {
        return translator().rebuildAll(
                                delegate().findAll(sort)
                            );
    }

    default Page<E> findAll(@NonNull Pageable pageable) {
        return delegate().findAll(pageable)
                            .map(translator()::rebuild);
    }

    default <S extends E> S save(@NonNull S entity) {
        Translator<E, P> translator = translator();
        P saved = delegate().save(translator.persist(entity));
        translator.merge(saved, entity);
        return entity;
    }

    default <S extends E> Iterable<S> saveAll(@NonNull Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    default Optional<E> findById(@NonNull PK id) {
        return delegate().findById(id)
                            .map(translator()::rebuild);
    }

    default boolean existsById(@NonNull PK id) {
        return delegate().existsById(id);
    }

    default Iterable<E> findAll() {
        return translator().rebuildAll(
                                delegate().findAll()
                            );
    }

    @NonNull
    default Iterable<E> findAllById(@NonNull Iterable<PK> ids) {
        return translator().rebuildAll(
                                delegate().findAllById(ids)
                            );
    }

    default long count() {
        return delegate().count();
    }

    default void deleteById(@NonNull PK id) {
        delegate().deleteById(id);
    }

    default void delete(@NonNull E entity) {
        delegate().delete(translator().persist(entity));
    }

    default void deleteAll(@NonNull Iterable<? extends E> entities) {
        delegate().deleteAll(translator().persistAll(entities));
    }

    default void deleteAll() {
        delegate().deleteAll();
    }
}
