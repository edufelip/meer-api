package com.edufelip.meer.web;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edufelip.meer.config.TestClockConfig;
import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.core.store.Social;
import com.edufelip.meer.core.store.ThriftStore;
import com.edufelip.meer.core.store.ThriftStorePhoto;
import org.hamcrest.Matchers;
import com.edufelip.meer.domain.CreateGuideContentUseCase;
import com.edufelip.meer.domain.GetGuideContentsByThriftStoreUseCase;
import com.edufelip.meer.domain.GetThriftStoreUseCase;
import com.edufelip.meer.domain.GetThriftStoresUseCase;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.domain.repo.CategoryRepository;
import com.edufelip.meer.domain.repo.StoreFeedbackRepository;
import com.edufelip.meer.domain.repo.ThriftStoreRepository;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import com.edufelip.meer.service.GcsStorageService;
import com.edufelip.meer.service.GuideContentEngagementService;
import com.edufelip.meer.service.StoreFeedbackService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ThriftStoreController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestClockConfig.class)
class ThriftStoreControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private GetThriftStoreUseCase getThriftStoreUseCase;
  @MockitoBean private GetThriftStoresUseCase getThriftStoresUseCase;
  @MockitoBean private GetGuideContentsByThriftStoreUseCase getGuideContentsByThriftStoreUseCase;
  @MockitoBean private CreateGuideContentUseCase createGuideContentUseCase;
  @MockitoBean private ThriftStoreRepository thriftStoreRepository;
  @MockitoBean private CategoryRepository categoryRepository;
  @MockitoBean private AuthUserRepository authUserRepository;
  @MockitoBean private TokenProvider tokenProvider;
  @MockitoBean private StoreFeedbackService storeFeedbackService;
  @MockitoBean private GcsStorageService gcsStorageService;
  @MockitoBean private StoreFeedbackRepository storeFeedbackRepository;
  @MockitoBean private GuideContentEngagementService guideContentEngagementService;

  @Test
  void adminDeleteStoreCleansRelationsAndAssets() throws Exception {
    UUID adminId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    AuthUser admin = new AuthUser();
    admin.setId(adminId);
    admin.setEmail("admin@example.com");
    admin.setDisplayName("Admin");
    admin.setPasswordHash("hash");
    admin.setRole(Role.ADMIN);

    AuthUser owner = new AuthUser();
    owner.setId(ownerId);
    owner.setEmail("owner@example.com");
    owner.setDisplayName("Owner");
    owner.setPasswordHash("hash");

    ThriftStore store = new ThriftStore();
    store.setId(storeId);
    store.setOwner(owner);
    owner.setOwnedThriftStore(store);

    ThriftStorePhoto photo =
        new ThriftStorePhoto(store, "https://storage.googleapis.com/bucket/photo.jpg", 0);
    store.setPhotos(List.of(photo));

    when(tokenProvider.parseAccessToken("admin-token"))
        .thenReturn(new TokenPayload(adminId, "admin@example.com", "Admin", Role.ADMIN));
    when(authUserRepository.findById(adminId)).thenReturn(Optional.of(admin));
    when(thriftStoreRepository.findById(storeId)).thenReturn(Optional.of(store));

    mockMvc
        .perform(
            delete("/stores/{id}", storeId)
                .header("Authorization", "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(authUserRepository, times(1)).deleteFavoritesByStoreId(storeId);
    verify(storeFeedbackRepository, times(1)).deleteByThriftStoreId(storeId);
    verify(authUserRepository, times(1)).save(owner);
    verify(gcsStorageService, times(1))
        .deleteByUrl("https://storage.googleapis.com/bucket/photo.jpg");
    verify(thriftStoreRepository, times(1)).delete(store);
  }

  @Test
  void createStoreIncludesWebsiteAndWhatsapp() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    AuthUser user = new AuthUser();
    user.setId(userId);
    user.setEmail("owner@example.com");
    user.setDisplayName("Owner");
    user.setPasswordHash("hash");
    user.setRole(Role.USER);

    when(tokenProvider.parseAccessToken("token"))
        .thenReturn(new TokenPayload(userId, "owner@example.com", "Owner", Role.USER));
    when(authUserRepository.findById(userId)).thenReturn(Optional.of(user));
    when(thriftStoreRepository.save(org.mockito.ArgumentMatchers.any(ThriftStore.class)))
        .thenAnswer(
            inv -> {
              ThriftStore store = inv.getArgument(0, ThriftStore.class);
              store.setId(storeId);
              return store;
            });

    String body =
        """
        {
          "name": "Social Store",
          "description": "Nice",
          "addressLine": "123 Road",
          "phone": "555-1111",
          "latitude": -23.0,
          "longitude": -46.0,
          "social": {
            "website": "https://example.com",
            "whatsapp": "https://wa.me/5511999999999"
          }
        }
        """;

    mockMvc
        .perform(
            post("/stores")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.website").value("https://example.com"))
        .andExpect(jsonPath("$.whatsapp").value("https://wa.me/5511999999999"));
  }

  @Test
  void updateStoreMergesSocialFields() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    AuthUser owner = new AuthUser();
    owner.setId(userId);
    owner.setEmail("owner@example.com");
    owner.setDisplayName("Owner");
    owner.setPasswordHash("hash");
    owner.setRole(Role.USER);

    ThriftStore store = new ThriftStore();
    store.setId(storeId);
    store.setOwner(owner);
    owner.setOwnedThriftStore(store);
    store.setSocial(new Social("https://facebook.com/store", "insta", "https://old.com", "wa123"));

    when(tokenProvider.parseAccessToken("token"))
        .thenReturn(new TokenPayload(userId, "owner@example.com", "Owner", Role.USER));
    when(authUserRepository.findById(userId)).thenReturn(Optional.of(owner));
    when(thriftStoreRepository.findById(storeId)).thenReturn(Optional.of(store));

    String body =
        """
        {
          "social": {
            "website": "https://new.com"
          }
        }
        """;

    mockMvc
        .perform(
            put("/stores/{id}", storeId)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.website").value("https://new.com"))
        .andExpect(jsonPath("$.facebook").value("https://facebook.com/store"))
        .andExpect(jsonPath("$.whatsapp").value("wa123"));
  }

  @Test
  void updateStoreClearsExplicitNullSocialFields() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    AuthUser owner = new AuthUser();
    owner.setId(userId);
    owner.setEmail("owner@example.com");
    owner.setDisplayName("Owner");
    owner.setPasswordHash("hash");
    owner.setRole(Role.USER);

    ThriftStore store = new ThriftStore();
    store.setId(storeId);
    store.setOwner(owner);
    owner.setOwnedThriftStore(store);
    store.setSocial(
        new Social("https://facebook.com/store", "insta", "https://old.com", "wa123"));

    when(tokenProvider.parseAccessToken("token"))
        .thenReturn(new TokenPayload(userId, "owner@example.com", "Owner", Role.USER));
    when(authUserRepository.findById(userId)).thenReturn(Optional.of(owner));
    when(thriftStoreRepository.findById(storeId)).thenReturn(Optional.of(store));

    String body =
        """
        {
          "social": {
            "instagram": null,
            "website": null
          }
        }
        """;

    mockMvc
        .perform(
            put("/stores/{id}", storeId)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.instagram").value(Matchers.nullValue()))
        .andExpect(jsonPath("$.website").value(Matchers.nullValue()))
        .andExpect(jsonPath("$.facebook").value("https://facebook.com/store"))
        .andExpect(jsonPath("$.whatsapp").value("wa123"));
  }

  @Test
  void updateStoreRejectsWebsiteWithoutDotCom() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    AuthUser owner = new AuthUser();
    owner.setId(userId);
    owner.setEmail("owner@example.com");
    owner.setDisplayName("Owner");
    owner.setPasswordHash("hash");
    owner.setRole(Role.USER);

    ThriftStore store = new ThriftStore();
    store.setId(storeId);
    store.setOwner(owner);
    owner.setOwnedThriftStore(store);

    when(tokenProvider.parseAccessToken("token"))
        .thenReturn(new TokenPayload(userId, "owner@example.com", "Owner", Role.USER));
    when(authUserRepository.findById(userId)).thenReturn(Optional.of(owner));
    when(thriftStoreRepository.findById(storeId)).thenReturn(Optional.of(store));

    String body =
        """
        {
          "social": {
            "website": "https://example.org"
          }
        }
        """;

    mockMvc
        .perform(
            put("/stores/{id}", storeId)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest());
  }
}
