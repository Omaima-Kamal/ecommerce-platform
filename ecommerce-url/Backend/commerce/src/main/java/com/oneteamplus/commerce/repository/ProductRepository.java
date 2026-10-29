package com.oneteamplus.commerce.repository;

import com.oneteamplus.commerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "sizes"})
    @Override
    Optional<Product> findById(Long id);

    @Query(value = """
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.category c
            LEFT JOIN p.sizes s
            WHERE (p.category.id = :categoryId OR :categoryId IS NULL)
              AND ((:minPrice IS NULL OR :maxPrice IS NULL) OR p.discountedPrice BETWEEN :minPrice AND :maxPrice)
              AND (:minDiscount IS NULL OR p.discountPercent >= :minDiscount)
              AND (:stock IS NULL OR (:stock = 'in_stock' AND p.quantity > 0) OR (:stock = 'out_of_stock' AND p.quantity < 1))
              AND (:colorsEmpty = true OR LOWER(p.color) IN :colors)
              AND (:sizesEmpty = true OR LOWER(s.name) IN :sizes)
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT p) FROM Product p
                    LEFT JOIN p.sizes s
                    WHERE (p.category.id = :categoryId OR :categoryId IS NULL)
                      AND ((:minPrice IS NULL OR :maxPrice IS NULL) OR p.discountedPrice BETWEEN :minPrice AND :maxPrice)
                      AND (:minDiscount IS NULL OR p.discountPercent >= :minDiscount)
                      AND (:stock IS NULL OR (:stock = 'in_stock' AND p.quantity > 0) OR (:stock = 'out_of_stock' AND p.quantity < 1))
                      AND (:colorsEmpty = true OR LOWER(p.color) IN :colors)
                      AND (:sizesEmpty = true OR LOWER(s.name) IN :sizes)
                    """)
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("minDiscount") Integer minDiscount,
            @Param("stock") String stock,
            @Param("colors") List<String> colors,
            @Param("colorsEmpty") boolean colorsEmpty,
            @Param("sizes") List<String> sizes,
            @Param("sizesEmpty") boolean sizesEmpty,
            Pageable pageable
    );
}