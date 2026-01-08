package com.edufelip.meer.domain;

import com.edufelip.meer.core.category.Category;
import com.edufelip.meer.domain.repo.CategoryRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;

public class GetCategoriesUseCase {
  private final CategoryRepository categoryRepository;

  public GetCategoriesUseCase(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Cacheable("categoriesAll")
  public List<Category> executeAll() {
    return categoryRepository.findAll();
  }
}
