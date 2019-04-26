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

package io.watchdog.samples.provider.user_center.infra.repository;

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
