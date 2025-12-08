package com.edufelip.meer.domain.repo;

import com.edufelip.meer.core.auth.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    AuthUser findByEmail(String email);

    Page<AuthUser> findByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(String email, String name, Pageable pageable);
}
