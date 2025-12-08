package com.edufelip.meer.web;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.domain.repo.SupportContactRepository;
import com.edufelip.meer.dto.PageResponse;
import com.edufelip.meer.dto.SupportContactDto;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestAttributes;

import java.util.List;

@RestController
@RequestMapping("/dashboard/support")
public class AdminSupportController {

    private final TokenProvider tokenProvider;
    private final AuthUserRepository authUserRepository;
    private final SupportContactRepository supportContactRepository;

    public AdminSupportController(TokenProvider tokenProvider,
                                  AuthUserRepository authUserRepository,
                                  SupportContactRepository supportContactRepository) {
        this.tokenProvider = tokenProvider;
        this.authUserRepository = authUserRepository;
        this.supportContactRepository = supportContactRepository;
    }

    @GetMapping("/contacts")
    public PageResponse<SupportContactDto> listContacts(@RequestHeader("Authorization") String authHeader,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int pageSize) {
        requireAdmin(authHeader);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(pageSize, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageRes = supportContactRepository.findAll(pageable);
        List<SupportContactDto> items = pageRes.getContent().stream()
                .map(c -> new SupportContactDto(c.getId(), c.getName(), c.getEmail(), c.getMessage(), c.getCreatedAt()))
                .toList();
        return new PageResponse<>(items, page + 1, pageRes.hasNext());
    }

    @GetMapping("/contacts/{id}")
    public SupportContactDto getContact(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Integer id) {
        requireAdmin(authHeader);
        var contact = supportContactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        return new SupportContactDto(contact.getId(), contact.getName(), contact.getEmail(), contact.getMessage(), contact.getCreatedAt());
    }

    @DeleteMapping("/contacts/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteContact(@RequestHeader("Authorization") String authHeader,
                                                                       @PathVariable Integer id) {
        requireAdmin(authHeader);
        var contact = supportContactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        supportContactRepository.delete(contact);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    private AuthUser requireAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
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
