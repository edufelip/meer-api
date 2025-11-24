package com.edufelip.meer.web;

import com.edufelip.meer.domain.GetGuideContentsByThriftStoreUseCase;
import com.edufelip.meer.domain.GetGuideContentUseCase;
import com.edufelip.meer.domain.GetThriftStoresUseCase;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.dto.HomeResponseDto;
import com.edufelip.meer.mapper.Mappers;
import com.edufelip.meer.service.StoreFeedbackService;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class HomeController {

    private final GetThriftStoresUseCase getThriftStoresUseCase;
    private final GetGuideContentUseCase getGuideContentUseCase;
    private final TokenProvider tokenProvider;
    private final AuthUserRepository authUserRepository;
    private final StoreFeedbackService storeFeedbackService;

    public HomeController(GetThriftStoresUseCase getThriftStoresUseCase,
                          GetGuideContentUseCase getGuideContentUseCase,
                          TokenProvider tokenProvider,
                          AuthUserRepository authUserRepository,
                          StoreFeedbackService storeFeedbackService) {
        this.getThriftStoresUseCase = getThriftStoresUseCase;
        this.getGuideContentUseCase = getGuideContentUseCase;
        this.tokenProvider = tokenProvider;
        this.authUserRepository = authUserRepository;
        this.storeFeedbackService = storeFeedbackService;
    }

    @GetMapping("/home")
    public HomeResponseDto home(@RequestHeader("Authorization") String authHeader) {
        var user = currentUser(authHeader);
        Set<Integer> favoriteIds = user.getFavorites().stream().map(f -> f.getId()).collect(java.util.stream.Collectors.toSet());

        var stores = getThriftStoresUseCase.execute();
        var summaries = storeFeedbackService.getSummaries(stores.stream().map(s -> s.getId()).toList());
        var featured = stores.stream()
                .limit(10)
                .map(s -> {
                    var summary = summaries.get(s.getId());
                    Double rating = summary != null ? summary.rating() : null;
                    Integer reviewCount = summary != null && summary.reviewCount() != null ? summary.reviewCount().intValue() : null;
                    return Mappers.toDto(s, false, favoriteIds.contains(s.getId()), rating, reviewCount);
                })
                .toList();
        var nearby = stores.stream()
                .limit(10)
                .map(s -> {
                    var summary = summaries.get(s.getId());
                    Double rating = summary != null ? summary.rating() : null;
                    Integer reviewCount = summary != null && summary.reviewCount() != null ? summary.reviewCount().intValue() : null;
                    return Mappers.toDto(s, false, favoriteIds.contains(s.getId()), rating, reviewCount);
                })
                .toList();
        var content = getGuideContentUseCase.executeAll().stream().map(Mappers::toDto).toList();

        return new HomeResponseDto(featured, nearby, content);
    }

    private com.edufelip.meer.core.auth.AuthUser currentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new InvalidTokenException();
        String token = authHeader.substring("Bearer ".length()).trim();
        TokenPayload payload;
        try {
            payload = tokenProvider.parseAccessToken(token);
        } catch (RuntimeException ex) {
            throw new InvalidTokenException();
        }
        return authUserRepository.findById(payload.getUserId()).orElseThrow(InvalidTokenException::new);
    }
}
