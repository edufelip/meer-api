package com.edufelip.meer.web;

import com.edufelip.meer.core.content.GuideContent;
import com.edufelip.meer.domain.GetGuideContentUseCase;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.domain.repo.GuideContentRepository;
import com.edufelip.meer.dto.GuideContentDto;
import com.edufelip.meer.dto.GuideTopDto;
import com.edufelip.meer.mapper.Mappers;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/contents")
public class GuideContentController {

    private final GetGuideContentUseCase getGuideContentUseCase;
    private final GuideContentRepository guideContentRepository;
    private final AuthUserRepository authUserRepository;
    private final TokenProvider tokenProvider;

    public GuideContentController(GetGuideContentUseCase getGuideContentUseCase,
                                  GuideContentRepository guideContentRepository,
                                  AuthUserRepository authUserRepository,
                                  TokenProvider tokenProvider) {
        this.getGuideContentUseCase = getGuideContentUseCase;
        this.guideContentRepository = guideContentRepository;
        this.authUserRepository = authUserRepository;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/top")
    public List<GuideTopDto> top(@RequestHeader("Authorization") String authHeader,
                                 @RequestParam(name = "limit", defaultValue = "10") int limit) {
        currentUser(authHeader); // enforce auth
        return getGuideContentUseCase.executeRecentTop10().stream()
                .limit(limit)
                .map(gc -> new GuideTopDto(gc.getId(), gc.getTitle(), gc.getDescription(), gc.getImageUrl()))
                .toList();
    }

    @GetMapping("/{id}")
    public GuideContentDto getById(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        currentUser(authHeader); // just to enforce auth
        var content = getGuideContentUseCase.execute(id);
        return content != null ? Mappers.toDto(content) : null;
    }

    @PutMapping("/{id}")
    public GuideContentDto update(@PathVariable Integer id,
                                  @RequestBody GuideContent body,
                                  @RequestHeader("Authorization") String authHeader) {
        var user = currentUser(authHeader);
        var content = guideContentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
        if (user.getOwnedThriftStore() == null || !user.getOwnedThriftStore().getId().equals(content.getThriftStore().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must own this store to update content");
        }
        content.setTitle(body.getTitle());
        content.setDescription(body.getDescription());
        content.setCategoryLabel(body.getCategoryLabel());
        content.setType(body.getType());
        content.setImageUrl(body.getImageUrl());
        guideContentRepository.save(content);
        return Mappers.toDto(content);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        var user = currentUser(authHeader);
        var content = guideContentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content not found"));
        if (user.getOwnedThriftStore() == null || !user.getOwnedThriftStore().getId().equals(content.getThriftStore().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must own this store to delete content");
        }
        guideContentRepository.delete(content);
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
