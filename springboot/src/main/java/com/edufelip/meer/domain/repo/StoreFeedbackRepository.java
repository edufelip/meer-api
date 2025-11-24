package com.edufelip.meer.domain.repo;

import com.edufelip.meer.core.store.StoreFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreFeedbackRepository extends JpaRepository<StoreFeedback, Integer> {
    Optional<StoreFeedback> findByUserIdAndThriftStoreId(Integer userId, Integer storeId);
    void deleteByUserId(Integer userId);

    interface AggregateView {
        Integer getStoreId();
        Double getAvgScore();
        Long getCnt();
    }

    @Query("select f.thriftStore.id as storeId, avg(f.score) as avgScore, count(f) as cnt from StoreFeedback f where f.thriftStore.id in :storeIds and f.score is not null group by f.thriftStore.id")
    List<AggregateView> aggregateByStoreIds(@Param("storeIds") List<Integer> storeIds);
}
