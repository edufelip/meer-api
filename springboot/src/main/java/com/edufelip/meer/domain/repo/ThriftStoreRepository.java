package com.edufelip.meer.domain.repo;

import com.edufelip.meer.core.store.ThriftStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThriftStoreRepository extends JpaRepository<ThriftStore, Integer> {

    @Query("select t from ThriftStore t where :categoryId in elements(t.categories)")
    Page<ThriftStore> findByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);
}
