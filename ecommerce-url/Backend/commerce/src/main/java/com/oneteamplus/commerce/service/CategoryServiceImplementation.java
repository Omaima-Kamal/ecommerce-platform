package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.model.Category;
import com.oneteamplus.commerce.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImplementation implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImplementation.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImplementation(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category findOrCreateTopLevel(String name) {
        Category existing = categoryRepository.findByNameIgnoreCase(name);
        if (existing != null) {
            return existing;
        }

        Category category = new Category();
        category.setName(name);
        category.setLevel(1);

        Category saved = categoryRepository.save(category);
        log.info("Created top-level category: {}", saved.getName());
        return saved;
    }

    @Override
    public Category findOrCreateSecondLevel(String name, Category parent) {
        Category existing = categoryRepository.findByNameIgnoreCaseAndParentCategoryId(name, parent.getId());
        if (existing != null) {
            return existing;
        }

        Category category = new Category();
        category.setName(name);
        category.setParentCategory(parent);
        category.setLevel(2);

        Category saved = categoryRepository.save(category);
        log.info("Created second-level category: {} (parentId={})", saved.getName(), parent.getId());
        return saved;
    }

    @Override
    public Category findOrCreateThirdLevel(String name, Category parent) {
        Category existing = categoryRepository.findByNameIgnoreCaseAndParentCategoryId(name, parent.getId());
        if (existing != null) {
            return existing;
        }

        Category category = new Category();
        category.setName(name);
        category.setParentCategory(parent);
        category.setLevel(3);

        Category saved = categoryRepository.save(category);
        log.info("Created third-level category: {} (parentId={})", saved.getName(), parent.getId());
        return saved;
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }
}