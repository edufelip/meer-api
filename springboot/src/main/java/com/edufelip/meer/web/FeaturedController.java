package com.edufelip.meer.web;

import com.edufelip.meer.domain.GetThriftStoresUseCase;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.dto.ThriftStoreDto;
import com.edufelip.meer.mapper.Mappers;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import com.edufelip.meer.service.StoreFeedbackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class FeaturedController {

    private final GetThriftStoresUseCase getThriftStoresUseCase;
    private final TokenProvider tokenProvider;
    private final AuthUserRepository authUserRepository;
    private final StoreFeedbackService storeFeedbackService;

    public FeaturedController(GetThriftStoresUseCase getThriftStoresUseCase,
                              TokenProvider tokenProvider,
                              AuthUserRepository authUserRepository,
                              StoreFeedbackService storeFeedbackService) {
        this.getThriftStoresUseCase = getThriftStoresUseCase;
        this.tokenProvider = tokenProvider;
        this.authUserRepository = authUserRepository;
        this.storeFeedbackService = storeFeedbackService;
    }

    @GetMapping("/featured")
    public java.util.List<ThriftStoreDto> featured(@RequestHeader("Authorization") String authHeader,
                                                   @RequestParam(name = "lat", required = false) Double lat,
                                                   @RequestParam(name = "lng", required = false) Double lng) {
        var user = currentUser(authHeader);
        Set<Integer> favoriteIds = user.getFavorites().stream().map(f -> f.getId()).collect(Collectors.toSet());

        var stores = getThriftStoresUseCase.executeRecentTop10();
        var summaries = storeFeedbackService.getSummaries(stores.stream().map(s -> s.getId()).toList());

        return stores.stream()
                .map(s -> {
                    var summary = summaries.get(s.getId());
                    Double rating = summary != null ? summary.rating() : null;
                    Integer reviewCount = summary != null && summary.reviewCount() != null ? summary.reviewCount().intValue() : null;
                    Double distanceMeters = (lat != null && lng != null && s.getLatitude() != null && s.getLongitude() != null)
                            ? distanceKm(lat, lng, s.getLatitude(), s.getLongitude()) * 1000
                            : null;
                    return Mappers.toDto(s, false, favoriteIds.contains(s.getId()), rating, reviewCount, distanceMeters);
                })
                .toList();
    }

    // Haversine
    private double distanceKm(double lat1, double lon1, Double lat2, Double lon2) {
        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
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
