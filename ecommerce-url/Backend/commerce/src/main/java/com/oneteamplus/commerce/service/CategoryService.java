package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.model.Category;

public interface CategoryService {
    Category findOrCreateTopLevel(String name);
    Category findOrCreateSecondLevel(String name, Category parent);
    Category findOrCreateThirdLevel(String name, Category parent);
    Category findByName(String name);
}