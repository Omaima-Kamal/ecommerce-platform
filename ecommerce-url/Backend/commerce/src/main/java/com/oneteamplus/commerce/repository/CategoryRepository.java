package com.oneteamplus.commerce.repository;

import com.oneteamplus.commerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Category findByName(String name);

//    @Query("SELECT c from Category c Where c.name=:name And c.parentCategory.name=:parentCategoryName")
//    public Category findByNameAndParent(@Param("name") String name, @Param("parentCategoryName") String parentCategoryName);
//
//    Category findByNameAndParentCategoryId(String name, Long parentCategoryId);
    Category findByNameIgnoreCase(String name);

    Category findByNameIgnoreCaseAndParentCategoryId(String name, Long parentCategoryId);


}
