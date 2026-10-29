package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> findProductByCategoryHandler(
            @RequestParam(required = false) String topLevelCategory,
            @RequestParam(required = false) String secondLevelCategory,
            @RequestParam String category,
            @RequestParam(required = false) List<String> color,
            @RequestParam(required = false) List<String> size,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false, defaultValue = "0") Integer minDiscount,
            @RequestParam(required = false, defaultValue = "price_low") String sort,
            @RequestParam(required = false) String stock,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("REST request to fetch filtered products. Category: {}, Sort: {}, Page: {}, Size: {}",
                category, sort, pageNumber, pageSize);
        log.debug("Filter parameters - Colors: {}, Sizes: {}, Price Range: [{}-{}], Min Discount: {}, Stock: {}",
                color, size, minPrice, maxPrice, minDiscount, stock);

        Page<Product> res = productService.getAllProducts(
                topLevelCategory, secondLevelCategory, category, color, size, minPrice, maxPrice,
                minDiscount, sort, stock, pageNumber, pageSize);

        log.info("Successfully retrieved {} products (Total Elements: {}, Total Pages: {})",
                res.getNumberOfElements(), res.getTotalElements(), res.getTotalPages());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/products/id/{productId}")
    public ResponseEntity<Product> findProductByIdHandler(@PathVariable Long productId) throws ProductException {
        log.info("REST request to fetch product details for product ID: {}", productId);

        Product product = productService.findProductById(productId);
        log.info("Successfully fetched details for product ID: {}", productId);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}