package com.edufelip.meer.web;

import com.edufelip.meer.core.content.GuideContent;
import com.edufelip.meer.core.store.ThriftStore;
import com.edufelip.meer.domain.CreateGuideContentUseCase;
import com.edufelip.meer.domain.CreateThriftStoreUseCase;
import com.edufelip.meer.domain.GetGuideContentsByThriftStoreUseCase;
import com.edufelip.meer.domain.GetThriftStoreUseCase;
import com.edufelip.meer.domain.GetThriftStoresUseCase;
import com.edufelip.meer.domain.repo.CategoryRepository;
import com.edufelip.meer.domain.repo.ThriftStoreRepository;
import com.edufelip.meer.dto.GuideContentDto;
import com.edufelip.meer.dto.PageResponse;
import com.edufelip.meer.dto.ThriftStoreDto;
import com.edufelip.meer.mapper.Mappers;
import com.edufelip.meer.service.StoreFeedbackService;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class ThriftStoreController {

    private final GetThriftStoreUseCase getThriftStoreUseCase;
    private final GetThriftStoresUseCase getThriftStoresUseCase;
    private final CreateThriftStoreUseCase createThriftStoreUseCase;
    private final GetGuideContentsByThriftStoreUseCase getGuideContentsByThriftStoreUseCase;
    private final CreateGuideContentUseCase createGuideContentUseCase;
    private final ThriftStoreRepository thriftStoreRepository;
    private final CategoryRepository categoryRepository;
    private final com.edufelip.meer.domain.repo.AuthUserRepository authUserRepository;
    private final TokenProvider tokenProvider;
    private final StoreFeedbackService storeFeedbackService;

    public ThriftStoreController(GetThriftStoreUseCase getThriftStoreUseCase,
                                 GetThriftStoresUseCase getThriftStoresUseCase,
                                 CreateThriftStoreUseCase createThriftStoreUseCase,
                                 GetGuideContentsByThriftStoreUseCase getGuideContentsByThriftStoreUseCase,
                                 CreateGuideContentUseCase createGuideContentUseCase,
                                 ThriftStoreRepository thriftStoreRepository,
                                 CategoryRepository categoryRepository,
                                 com.edufelip.meer.domain.repo.AuthUserRepository authUserRepository,
                                 TokenProvider tokenProvider,
                                 StoreFeedbackService storeFeedbackService) {
        this.getThriftStoreUseCase = getThriftStoreUseCase;
        this.getThriftStoresUseCase = getThriftStoresUseCase;
        this.createThriftStoreUseCase = createThriftStoreUseCase;
        this.getGuideContentsByThriftStoreUseCase = getGuideContentsByThriftStoreUseCase;
        this.createGuideContentUseCase = createGuideContentUseCase;
        this.thriftStoreRepository = thriftStoreRepository;
        this.categoryRepository = categoryRepository;
        this.authUserRepository = authUserRepository;
        this.tokenProvider = tokenProvider;
        this.storeFeedbackService = storeFeedbackService;
    }

    @GetMapping
    public PageResponse<ThriftStoreDto> getStores(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination params");
        }
        var user = currentUser(authHeader);
        var favorites = user.getFavorites();
        var pageable = PageRequest.of(page - 1, pageSize);
        java.util.List<ThriftStore> storesPage;
        boolean hasNext;

        if ("nearby".equalsIgnoreCase(type)) {
            var result = getThriftStoresUseCase.executePaged(page - 1, pageSize);
            storesPage = result.getContent();
            hasNext = result.hasNext();
        }
        else if (categoryId != null) {
            if (categoryRepository.findById(categoryId).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            }
            var result = thriftStoreRepository.findByCategoryId(categoryId, pageable);
            storesPage = result.getContent();
            hasNext = result.hasNext();
        } else {
            var result = getThriftStoresUseCase.executePaged(page - 1, pageSize);
            storesPage = result.getContent();
            hasNext = result.hasNext();
        }

        var ids = storesPage.stream().map(ThriftStore::getId).toList();
        var summaries = storeFeedbackService.getSummaries(ids);

        var items = storesPage.stream()
                .map(store -> {
                    var summary = summaries.get(store.getId());
                    Double rating = summary != null ? summary.rating() : null;
                    Integer reviewCount = summary != null && summary.reviewCount() != null ? summary.reviewCount().intValue() : null;
                    boolean isFav = favorites.stream().anyMatch(f -> f.getId().equals(store.getId()));
                    return Mappers.toDto(store, false, isFav, rating, reviewCount);
                })
                .toList();
        return new PageResponse<>(items, page, hasNext);
    }

    @GetMapping("/{id}")
    public ThriftStoreDto getById(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        var user = currentUser(authHeader);
        var store = getThriftStoreUseCase.execute(id);
        if (store == null) return null;
        var summary = storeFeedbackService.getSummaries(java.util.List.of(store.getId())).get(store.getId());
        Double rating = summary != null ? summary.rating() : null;
        Integer reviewCount = summary != null && summary.reviewCount() != null ? summary.reviewCount().intValue() : null;
        boolean isFav = user.getFavorites().stream().anyMatch(f -> f.getId().equals(store.getId()));
        return Mappers.toDto(store, true, isFav, rating, reviewCount);
    }

    @PostMapping
    public ThriftStoreDto create(@RequestBody ThriftStore thriftStore) {
        return Mappers.toDto(createThriftStoreUseCase.execute(thriftStore), true);
    }

    @GetMapping("/{storeId}/contents")
    public List<GuideContentDto> getContentsByThriftStoreId(@PathVariable Integer storeId) {
        return getGuideContentsByThriftStoreUseCase.execute(storeId).stream().map(Mappers::toDto).toList();
    }

    @PostMapping("/{storeId}/contents")
    public GuideContentDto createGuideContent(
            @PathVariable Integer storeId,
            @RequestBody GuideContent guideContent,
            @RequestHeader("Authorization") String authHeader
    ) {
        var user = currentUser(authHeader);
        if (user.getOwnedThriftStore() == null || !user.getOwnedThriftStore().getId().equals(storeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must own this store to add content");
        }
        var thriftStore = thriftStoreRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Thrift store not found"));
        var contentWithStore = new GuideContent(
                guideContent.getId(),
                guideContent.getTitle(),
                guideContent.getDescription(),
                guideContent.getCategoryLabel(),
                guideContent.getType(),
                guideContent.getImageUrl(),
                thriftStore);
        return Mappers.toDto(createGuideContentUseCase.execute(contentWithStore));
    }

    private Integer extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new InvalidTokenException();
        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            TokenPayload payload = tokenProvider.parseAccessToken(token);
            return payload.getUserId();
        } catch (RuntimeException ex) {
            throw new InvalidTokenException();
        }
    }

    private com.edufelip.meer.core.auth.AuthUser currentUser(String authHeader) {
        Integer userId = extractUserId(authHeader);
        return authUserRepository.findById(userId).orElseThrow(InvalidTokenException::new);
    }
}
