package io.watchdog.samples.provider.user_center.infrastructure.repository;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 领域实体类与持久化实体类之间相互转化的转化器
 * @param <E> 领域实体类
 * @param <P> 持久化实体类
 */
public interface Translator<E, P> {

    E rebuild(P po);

    default Iterable<E> rebuildAll(Iterable<P> pos) {
        return StreamSupport.stream(pos.spliterator(), false)
                .map(this::rebuild)
                .collect(Collectors.toList());
    }

    <S extends E> P persist(S e);

    default Iterable<? extends P> persistAll(Iterable<? extends E> es) {
        return StreamSupport.stream(es.spliterator(), false)
                .map(this::persist)
                .collect(Collectors.toList());
    }

    <S extends E> S merge(P save, S entity);

}
