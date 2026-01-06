package com.edufelip.meer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.content.GuideContent;
import com.edufelip.meer.core.content.GuideContentComment;
import com.edufelip.meer.domain.repo.GuideContentCommentRepository;
import com.edufelip.meer.domain.repo.GuideContentRepository;
import com.edufelip.meer.support.TestFixtures;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GuideContentModerationServiceTest {

  @Test
  void softDeleteCommentSetsFieldsAndDecrementsCount() {
    GuideContentRepository contentRepository = Mockito.mock(GuideContentRepository.class);
    GuideContentCommentRepository commentRepository =
        Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    GuideContentModerationService service =
        new GuideContentModerationService(contentRepository, commentRepository, clock);

    AuthUser actor = new AuthUser();
    actor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(5);

    GuideContentComment comment = new GuideContentComment(actor, content, "Hello");
    comment.setId(9);

    Mockito.when(commentRepository.save(Mockito.any(GuideContentComment.class)))
        .thenAnswer(inv -> inv.getArgument(0, GuideContentComment.class));

    GuideContentComment deleted = service.softDeleteComment(comment, actor, "spam");

    assertThat(deleted.getDeletedAt()).isEqualTo(TestFixtures.fixedInstant());
    assertThat(deleted.getDeletedBy()).isEqualTo(actor);
    assertThat(deleted.getDeletedReason()).isEqualTo("spam");
    Mockito.verify(contentRepository).decrementCommentCount(5);
  }

  @Test
  void restoreCommentClearsFieldsAndIncrementsCount() {
    GuideContentRepository contentRepository = Mockito.mock(GuideContentRepository.class);
    GuideContentCommentRepository commentRepository =
        Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    GuideContentModerationService service =
        new GuideContentModerationService(contentRepository, commentRepository, clock);

    AuthUser actor = new AuthUser();
    actor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(5);

    GuideContentComment comment = new GuideContentComment(actor, content, "Hello");
    comment.setDeletedAt(TestFixtures.fixedInstant());
    comment.setDeletedBy(actor);
    comment.setDeletedReason("spam");

    Mockito.when(commentRepository.save(Mockito.any(GuideContentComment.class)))
        .thenAnswer(inv -> inv.getArgument(0, GuideContentComment.class));

    GuideContentComment restored = service.restoreComment(comment);

    assertThat(restored.getDeletedAt()).isNull();
    assertThat(restored.getDeletedBy()).isNull();
    assertThat(restored.getDeletedReason()).isNull();
    Mockito.verify(contentRepository).incrementCommentCount(5);
  }

  @Test
  void softDeleteCommentIsIdempotent() {
    GuideContentRepository contentRepository = Mockito.mock(GuideContentRepository.class);
    GuideContentCommentRepository commentRepository =
        Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    GuideContentModerationService service =
        new GuideContentModerationService(contentRepository, commentRepository, clock);

    AuthUser actor = new AuthUser();
    actor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(5);

    GuideContentComment comment = new GuideContentComment(actor, content, "Hello");
    comment.setDeletedAt(TestFixtures.fixedInstant());

    service.softDeleteComment(comment, actor, "spam");

    Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    Mockito.verify(contentRepository, Mockito.never()).decrementCommentCount(Mockito.any());
  }

  @Test
  void softDeleteAndRestoreContent() {
    GuideContentRepository contentRepository = Mockito.mock(GuideContentRepository.class);
    GuideContentCommentRepository commentRepository =
        Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    GuideContentModerationService service =
        new GuideContentModerationService(contentRepository, commentRepository, clock);

    AuthUser actor = new AuthUser();
    actor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(7);

    Mockito.when(contentRepository.save(Mockito.any(GuideContent.class)))
        .thenAnswer(inv -> inv.getArgument(0, GuideContent.class));

    GuideContent deleted = service.softDeleteContent(content, actor, "spam");
    assertThat(deleted.getDeletedAt()).isEqualTo(TestFixtures.fixedInstant());
    assertThat(deleted.getDeletedBy()).isEqualTo(actor);
    assertThat(deleted.getDeletedReason()).isEqualTo("spam");

    GuideContent restored = service.restoreContent(content);
    assertThat(restored.getDeletedAt()).isNull();
    assertThat(restored.getDeletedBy()).isNull();
    assertThat(restored.getDeletedReason()).isNull();
  }
}
