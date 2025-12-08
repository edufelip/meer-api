package com.edufelip.meer.web;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.core.category.Category;
import com.edufelip.meer.domain.repo.CategoryRepository;
import com.edufelip.meer.dto.CategoryDto;
import com.edufelip.meer.dto.CategoryUpsertRequest;
import com.edufelip.meer.dto.PageResponse;
import com.edufelip.meer.security.AdminContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/dashboard/categories")
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    public AdminCategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public PageResponse<CategoryDto> list(@RequestHeader("Authorization") String authHeader,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "50") int pageSize) {
        requireAdmin(authHeader);
        if (page < 0 || pageSize < 1 || pageSize > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination params");
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        var pageRes = categoryRepository.findAll(pageable);
        var items = pageRes.getContent().stream()
                .map(c -> new CategoryDto(c.getId(), c.getNameStringId(), c.getImageResId(), c.getCreatedAt()))
                .toList();
        return new PageResponse<>(items, page, pageRes.hasNext());
    }

    @PostMapping
    public CategoryDto create(@RequestHeader("Authorization") String authHeader,
                              @RequestBody CategoryUpsertRequest body) {
        requireAdmin(authHeader);
        validateBody(body, true, null);
        if (categoryRepository.existsById(body.id())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        Category category = new Category(body.id(), body.nameStringId(), body.imageResId());
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getNameStringId(), saved.getImageResId(), saved.getCreatedAt());
    }

    @PutMapping("/{id}")
    public CategoryDto update(@RequestHeader("Authorization") String authHeader,
                              @PathVariable String id,
                              @RequestBody CategoryUpsertRequest body) {
        requireAdmin(authHeader);
        validateBody(body, false, id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        category.setNameStringId(body.nameStringId());
        category.setImageResId(body.imageResId());
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getNameStringId(), saved.getImageResId(), saved.getCreatedAt());
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> delete(@RequestHeader("Authorization") String authHeader,
                                                                @PathVariable String id) {
        requireAdmin(authHeader);
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        categoryRepository.deleteById(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    private AuthUser requireAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }
        AuthUser user = AdminContext.currentAdmin()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing admin context"));
        Role role = user.getRole() != null ? user.getRole() : Role.USER;
        if (role != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }
        return user;
    }

    private void validateBody(CategoryUpsertRequest body, boolean requireId, String pathId) {
        if (body == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
        }
        if ((requireId && (body.id() == null || body.id().isBlank())) || (pathId != null && (body.id() == null || !body.id().equals(pathId)))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required and must match path");
        }
        if (body.nameStringId() == null || body.nameStringId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nameStringId is required");
        }
        if (body.imageResId() == null || body.imageResId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageResId is required");
        }
    }
}
