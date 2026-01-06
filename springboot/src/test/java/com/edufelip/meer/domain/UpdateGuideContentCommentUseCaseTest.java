package com.edufelip.meer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.content.GuideContent;
import com.edufelip.meer.core.content.GuideContentComment;
import com.edufelip.meer.domain.repo.GuideContentCommentRepository;
import com.edufelip.meer.support.TestFixtures;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class UpdateGuideContentCommentUseCaseTest {

  @Test
  void updatesBodyAndSetsEditedMetadata() {
    GuideContentCommentRepository repository = Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    UpdateGuideContentCommentUseCase useCase =
        new UpdateGuideContentCommentUseCase(repository, clock);

    AuthUser editor = new AuthUser();
    editor.setId(UUID.randomUUID());
    editor.setDisplayName("Editor");

    GuideContent content = new GuideContent();
    content.setId(1);

    GuideContentComment comment = new GuideContentComment(editor, content, "Old");
    comment.setId(10);

    Mockito.when(repository.save(Mockito.any(GuideContentComment.class)))
        .thenAnswer(inv -> inv.getArgument(0, GuideContentComment.class));

    GuideContentComment updated = useCase.execute(comment, "<b>New</b>", editor);

    assertThat(updated.getBody()).isEqualTo("New");
    assertThat(updated.getEditedAt()).isEqualTo(TestFixtures.fixedInstant());
    assertThat(updated.getEditedBy()).isEqualTo(editor);
    assertThat(updated.getUpdatedAt()).isEqualTo(TestFixtures.fixedInstant());
  }

  @Test
  void rejectsBlankBody() {
    GuideContentCommentRepository repository = Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    UpdateGuideContentCommentUseCase useCase =
        new UpdateGuideContentCommentUseCase(repository, clock);

    AuthUser editor = new AuthUser();
    editor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(1);

    GuideContentComment comment = new GuideContentComment(editor, content, "Old");

    assertThatThrownBy(() -> useCase.execute(comment, "   ", editor))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("comment body is required");
  }

  @Test
  void rejectsTooLongBody() {
    GuideContentCommentRepository repository = Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    UpdateGuideContentCommentUseCase useCase =
        new UpdateGuideContentCommentUseCase(repository, clock);

    AuthUser editor = new AuthUser();
    editor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(1);

    GuideContentComment comment = new GuideContentComment(editor, content, "Old");

    String tooLong = "a".repeat(121);

    assertThatThrownBy(() -> useCase.execute(comment, tooLong, editor))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("comment must be 120 characters or less");
  }

  @Test
  void noOpWhenBodyUnchanged() {
    GuideContentCommentRepository repository = Mockito.mock(GuideContentCommentRepository.class);
    Clock clock = Clock.fixed(TestFixtures.fixedInstant(), ZoneOffset.UTC);
    UpdateGuideContentCommentUseCase useCase =
        new UpdateGuideContentCommentUseCase(repository, clock);

    AuthUser editor = new AuthUser();
    editor.setId(UUID.randomUUID());

    GuideContent content = new GuideContent();
    content.setId(1);

    GuideContentComment comment = new GuideContentComment(editor, content, "Same");

    GuideContentComment updated = useCase.execute(comment, "Same", editor);

    assertThat(updated).isSameAs(comment);
    Mockito.verify(repository, Mockito.never()).save(Mockito.any());
  }
}
