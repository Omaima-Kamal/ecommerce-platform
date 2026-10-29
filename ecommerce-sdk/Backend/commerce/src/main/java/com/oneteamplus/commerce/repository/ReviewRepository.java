package com.oneteamplus.commerce.repository;

import com.oneteamplus.commerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("Select r From Review r Where r.product.id=:productId")
    public List<Review> getAllProductsReview(@Param("productId")Long productId);
}
