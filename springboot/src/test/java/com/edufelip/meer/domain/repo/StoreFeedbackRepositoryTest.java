package com.edufelip.meer.domain.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.store.StoreFeedback;
import com.edufelip.meer.core.store.ThriftStore;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StoreFeedbackRepositoryTest {

  @Autowired private StoreFeedbackRepository storeFeedbackRepository;
  @Autowired private ThriftStoreRepository thriftStoreRepository;
  @Autowired private AuthUserRepository authUserRepository;

  @Test
  void findRatingsByStoreIdOrdersByCreatedAtAndSkipsNullScores() {
    ThriftStore store = new ThriftStore();
    store.setName("Test Store");
    store.setAddressLine("123 Road");
    store = thriftStoreRepository.save(store);

    AuthUser user1 = new AuthUser();
    user1.setEmail("a@example.com");
    user1.setDisplayName("A");
    user1.setPasswordHash("hash");
    user1 = authUserRepository.save(user1);

    AuthUser user2 = new AuthUser();
    user2.setEmail("b@example.com");
    user2.setDisplayName("B");
    user2.setPasswordHash("hash");
    user2 = authUserRepository.save(user2);

    StoreFeedback older = new StoreFeedback(user1, store, 4, "old");
    older.setCreatedAt(Instant.parse("2024-01-01T10:00:00Z"));
    storeFeedbackRepository.save(older);

    StoreFeedback newer = new StoreFeedback(user2, store, 5, "new");
    newer.setCreatedAt(Instant.parse("2024-01-02T10:00:00Z"));
    storeFeedbackRepository.save(newer);

    AuthUser user3 = new AuthUser();
    user3.setEmail("c@example.com");
    user3.setDisplayName("C");
    user3.setPasswordHash("hash");
    user3 = authUserRepository.save(user3);

    StoreFeedback textOnly = new StoreFeedback(user3, store, null, "text");
    textOnly.setCreatedAt(Instant.parse("2024-01-03T10:00:00Z"));
    storeFeedbackRepository.save(textOnly);

    var slice = storeFeedbackRepository.findRatingsByStoreId(store.getId(), PageRequest.of(0, 10));

    assertThat(slice.getContent()).hasSize(2);
    assertThat(slice.getContent().get(0).body()).isEqualTo("new");
    assertThat(slice.getContent().get(1).body()).isEqualTo("old");
    assertThat(slice.getContent()).extracting("score").doesNotContainNull();
  }

  @Test
  void aggregateByStoreIdsComputesAvgAndCountForScoredOnly() {
    ThriftStore store = new ThriftStore();
    store.setName("Aggregate Store");
    store.setAddressLine("456 Road");
    store = thriftStoreRepository.save(store);

    AuthUser user1 = new AuthUser();
    user1.setEmail("c@example.com");
    user1.setDisplayName("C");
    user1.setPasswordHash("hash");
    user1 = authUserRepository.save(user1);

    AuthUser user2 = new AuthUser();
    user2.setEmail("d@example.com");
    user2.setDisplayName("D");
    user2.setPasswordHash("hash");
    user2 = authUserRepository.save(user2);

    AuthUser user3 = new AuthUser();
    user3.setEmail("e@example.com");
    user3.setDisplayName("E");
    user3.setPasswordHash("hash");
    user3 = authUserRepository.save(user3);

    storeFeedbackRepository.save(new StoreFeedback(user1, store, 4, "ok"));
    storeFeedbackRepository.save(new StoreFeedback(user2, store, 2, "bad"));
    storeFeedbackRepository.save(new StoreFeedback(user3, store, null, "text"));

    List<StoreFeedbackRepository.AggregateView> results =
        storeFeedbackRepository.aggregateByStoreIds(List.of(store.getId()));

    assertThat(results).hasSize(1);
    StoreFeedbackRepository.AggregateView view = results.get(0);
    assertThat(view.getStoreId()).isEqualTo(store.getId());
    assertThat(view.getCnt()).isEqualTo(2);
    assertThat(view.getAvgScore()).isEqualTo(3.0);
  }
}
