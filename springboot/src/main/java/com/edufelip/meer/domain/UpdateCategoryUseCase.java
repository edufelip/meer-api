package com.edufelip.meer.domain;

import com.edufelip.meer.core.category.Category;
import com.edufelip.meer.domain.repo.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;

public class UpdateCategoryUseCase {
  private final CategoryRepository categoryRepository;

  public UpdateCategoryUseCase(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @CacheEvict(cacheNames = "categoriesAll", allEntries = true)
  public Category execute(Category category) {
    return categoryRepository.save(category);
  }
}
