package com.edufelip.meer.web;

import com.edufelip.meer.domain.auth.GetProfileUseCase;
import com.edufelip.meer.domain.auth.UpdateProfileUseCase;
import com.edufelip.meer.dto.ProfileDto;
import com.edufelip.meer.dto.UpdateProfileRequest;
import com.edufelip.meer.mapper.Mappers;
import com.edufelip.meer.security.token.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    public ProfileController(GetProfileUseCase getProfileUseCase,
                             UpdateProfileUseCase updateProfileUseCase) {
        this.getProfileUseCase = getProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
    }

    @GetMapping
    public ProfileDto getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = extractBearer(authHeader);
        var user = getProfileUseCase.execute(token);
        return Mappers.toProfileDto(user, true);
    }

    @PutMapping
    public ProfileDto updateProfile(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody UpdateProfileRequest body) {
        String token = extractBearer(authHeader);
        if (body == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body required");
        var user = updateProfileUseCase.execute(token, body);
        return Mappers.toProfileDto(user, true);
    }

    private String extractBearer(String header) {
        if (header == null || !header.startsWith("Bearer ")) throw new InvalidTokenException();
        return header.substring("Bearer ".length()).trim();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalid(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new com.edufelip.meer.dto.ErrorDto(ex.getMessage()));
    }
}
