package com.edufelip.meer.web;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.domain.repo.GuideContentRepository;
import com.edufelip.meer.domain.repo.ThriftStoreRepository;
import com.edufelip.meer.dto.AdminUserDto;
import com.edufelip.meer.dto.GuideContentDto;
import com.edufelip.meer.dto.PageResponse;
import com.edufelip.meer.dto.ThriftStoreDto;
import com.edufelip.meer.dto.DashboardStoreSummaryDto;
import com.edufelip.meer.dto.ProfileDto;
import com.edufelip.meer.mapper.Mappers;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestAttributes;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
public class AdminDashboardController {

    private final TokenProvider tokenProvider;
    private final AuthUserRepository authUserRepository;
    private final ThriftStoreRepository thriftStoreRepository;
    private final GuideContentRepository guideContentRepository;

    public AdminDashboardController(TokenProvider tokenProvider,
                                    AuthUserRepository authUserRepository,
                                    ThriftStoreRepository thriftStoreRepository,
                                    GuideContentRepository guideContentRepository) {
        this.tokenProvider = tokenProvider;
        this.authUserRepository = authUserRepository;
        this.thriftStoreRepository = thriftStoreRepository;
        this.guideContentRepository = guideContentRepository;
    }

    @GetMapping("/stores")
    public PageResponse<DashboardStoreSummaryDto> listStores(@RequestHeader("Authorization") String authHeader,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int pageSize,
                                                             @RequestParam(required = false) String q,
                                                             @RequestParam(name = "search", required = false) String search,
                                                             @RequestParam(defaultValue = "newest") String sort) {
        requireAdmin(authHeader);
        String term = (search != null && !search.isBlank()) ? search : q;
        Sort s = "oldest".equalsIgnoreCase(sort)
                ? Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt")
                : Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(pageSize, 100), s);
        var pageRes = (term != null && !term.isBlank())
                ? thriftStoreRepository.search(term.trim(), pageable)
                : thriftStoreRepository.findAll(pageable);
        List<DashboardStoreSummaryDto> items = pageRes.getContent().stream()
                .map(ts -> new DashboardStoreSummaryDto(ts.getId(), ts.getName(), ts.getAddressLine(), ts.getCreatedAt()))
                .toList();
        return new PageResponse<>(items, page + 1, pageRes.hasNext());
    }

    @GetMapping("/contents")
    public PageResponse<GuideContentDto> listContents(@RequestHeader("Authorization") String authHeader,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int pageSize,
                                                      @RequestParam(required = false) String q,
                                                      @RequestParam(defaultValue = "newest") String sort) {
        requireAdmin(authHeader);
        Sort s = "oldest".equalsIgnoreCase(sort)
                ? Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt")
                : Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(pageSize, 100), s);
        var pageRes = (q != null && !q.isBlank())
                ? guideContentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable)
                : guideContentRepository.findAll(pageable);
        List<GuideContentDto> items = pageRes.getContent().stream().map(Mappers::toDto).toList();
        return new PageResponse<>(items, page + 1, pageRes.hasNext());
    }

    @GetMapping("/users")
    public PageResponse<AdminUserDto> listUsers(@RequestHeader("Authorization") String authHeader,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int pageSize,
                                                @RequestParam(required = false) String q,
                                                @RequestParam(defaultValue = "newest") String sort) {
        requireAdmin(authHeader);
        Sort s = "oldest".equalsIgnoreCase(sort)
                ? Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt")
                : Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(pageSize, 100), s);
        var pageRes = (q != null && !q.isBlank())
                ? authUserRepository.findByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(q, q, pageable)
                : authUserRepository.findAll(pageable);
        List<AdminUserDto> items = pageRes.getContent().stream()
                .map(u -> new AdminUserDto(
                        u.getId().toString(),
                        u.getDisplayName(),
                        u.getEmail(),
                        (u.getRole() != null ? u.getRole() : Role.USER).name(),
                        u.getCreatedAt(),
                        u.getPhotoUrl()
                ))
                .toList();
        return new PageResponse<>(items, page + 1, pageRes.hasNext());
    }

    @GetMapping("/users/{id}")
    public ProfileDto getUser(@RequestHeader("Authorization") String authHeader,
                              @PathVariable java.util.UUID id) {
        requireAdmin(authHeader);
        var user = authUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return Mappers.toProfileDto(user, true);
    }

    private AuthUser requireAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
        // Reuse admin user loaded by DashboardAdminGuardFilter when present
        Object cached = RequestContextHolder.currentRequestAttributes()
                .getAttribute("adminUser", RequestAttributes.SCOPE_REQUEST);

        AuthUser user;
        if (cached instanceof AuthUser cachedUser) {
            user = cachedUser;
        } else {
            String token = authHeader.substring("Bearer ".length()).trim();
            TokenPayload payload = tokenProvider.parseAccessToken(token);
            user = authUserRepository.findById(payload.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        }

        Role effectiveRole = user.getRole() != null ? user.getRole() : Role.USER;
        if (effectiveRole != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }
        return user;
    }
}
