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

    public ThriftStoreController(GetThriftStoreUseCase getThriftStoreUseCase,
                                 GetThriftStoresUseCase getThriftStoresUseCase,
                                 CreateThriftStoreUseCase createThriftStoreUseCase,
                                 GetGuideContentsByThriftStoreUseCase getGuideContentsByThriftStoreUseCase,
                                 CreateGuideContentUseCase createGuideContentUseCase,
                                 ThriftStoreRepository thriftStoreRepository,
                                 CategoryRepository categoryRepository,
                                 com.edufelip.meer.domain.repo.AuthUserRepository authUserRepository,
                                 TokenProvider tokenProvider) {
        this.getThriftStoreUseCase = getThriftStoreUseCase;
        this.getThriftStoresUseCase = getThriftStoresUseCase;
        this.createThriftStoreUseCase = createThriftStoreUseCase;
        this.getGuideContentsByThriftStoreUseCase = getGuideContentsByThriftStoreUseCase;
        this.createGuideContentUseCase = createGuideContentUseCase;
        this.thriftStoreRepository = thriftStoreRepository;
        this.categoryRepository = categoryRepository;
        this.authUserRepository = authUserRepository;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping
    public List<ThriftStoreDto> getAll() {
        return getThriftStoresUseCase.execute().stream().map(store -> Mappers.toDto(store, false)).toList();
    }

    @GetMapping("/featured")
    public List<ThriftStoreDto> getFeatured() {
        // Placeholder: return all for now; plug ranking logic here later
        return getThriftStoresUseCase.execute().stream().map(store -> Mappers.toDto(store, false)).toList();
    }

    @GetMapping("/nearby")
    public List<ThriftStoreDto> getNearby() {
        // Placeholder: return all; attach geo-sorting when location data is provided
        return getThriftStoresUseCase.execute().stream().map(store -> Mappers.toDto(store, false)).toList();
    }

    @GetMapping("/favorites")
    public List<ThriftStoreDto> getFavorites() {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header required");
    }

    @GetMapping("/{id}")
    public ThriftStoreDto getById(@PathVariable Integer id) {
        var store = getThriftStoreUseCase.execute(id);
        return store != null ? Mappers.toDto(store, true) : null;
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
            @RequestBody GuideContent guideContent
    ) {
        var thriftStore = thriftStoreRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Thrift store not found"));
        var contentWithStore = new GuideContent(guideContent.getId(), guideContent.getTitle(), guideContent.getDescription(), guideContent.getCategoryLabel(), guideContent.getImageUrl(), thriftStore);
        return Mappers.toDto(createGuideContentUseCase.execute(contentWithStore));
    }

    @GetMapping(params = {"categoryId"})
    public PageResponse<ThriftStoreDto> getStoresByCategory(
            @RequestParam String categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = extractUserId(authHeader);
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination params");
        }
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        var pageable = PageRequest.of(page - 1, pageSize);
        var result = thriftStoreRepository.findByCategoryId(categoryId, pageable);
        var user = authUserRepository.findById(userId).orElse(null);
        java.util.Set<com.edufelip.meer.core.store.ThriftStore> favorites = user != null
                ? user.getFavorites()
                : java.util.Collections.emptySet();

        var items = result.getContent().stream()
                .map(store -> Mappers.toDto(store, false, favorites.stream().anyMatch(f -> f.getId().equals(store.getId()))))
                .toList();
        return new PageResponse<>(items, page, result.hasNext());
    }

    @GetMapping("/favorites")
    public List<ThriftStoreDto> getUserFavorites(@RequestHeader("Authorization") String authHeader) {
        var user = currentUser(authHeader);
        return user.getFavorites().stream()
                .map(store -> Mappers.toDto(store, false, true))
                .toList();
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
