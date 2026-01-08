package com.edufelip.meer.domain;

import com.edufelip.meer.domain.repo.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;

public class DeleteCategoryUseCase {
  private final CategoryRepository categoryRepository;

  public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @CacheEvict(cacheNames = "categoriesAll", allEntries = true)
  public void execute(String id) {
    categoryRepository.deleteById(id);
  }
}
