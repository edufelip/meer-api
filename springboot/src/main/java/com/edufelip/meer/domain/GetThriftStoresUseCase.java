package com.edufelip.meer.domain;

import com.edufelip.meer.core.store.ThriftStore;
import com.edufelip.meer.domain.repo.ThriftStoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class GetThriftStoresUseCase {
    private final ThriftStoreRepository thriftStoreRepository;

    public GetThriftStoresUseCase(ThriftStoreRepository thriftStoreRepository) {
        this.thriftStoreRepository = thriftStoreRepository;
    }

    public List<ThriftStore> execute() {
        return thriftStoreRepository.findAll();
    }

    public Page<ThriftStore> executePaged(int page, int pageSize) {
        return thriftStoreRepository.findAll(PageRequest.of(page, pageSize));
    }
}
