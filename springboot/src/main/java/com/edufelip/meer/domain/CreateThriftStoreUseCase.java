package com.edufelip.meer.domain;

import com.edufelip.meer.core.store.ThriftStore;
import com.edufelip.meer.domain.repo.ThriftStoreRepository;
import org.springframework.cache.annotation.CacheEvict;

public class CreateThriftStoreUseCase {
    private final ThriftStoreRepository thriftStoreRepository;

    public CreateThriftStoreUseCase(ThriftStoreRepository thriftStoreRepository) {
        this.thriftStoreRepository = thriftStoreRepository;
    }

    @CacheEvict(cacheNames = "featuredTop10", allEntries = true)
    public ThriftStore execute(ThriftStore thriftStore) {
        return thriftStoreRepository.save(thriftStore);
    }
}
