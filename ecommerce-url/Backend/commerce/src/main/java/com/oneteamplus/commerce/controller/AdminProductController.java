package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.payload.request.CreateProductRequest;
import com.oneteamplus.commerce.payload.response.ApiResponse;
import com.oneteamplus.commerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private static final Logger log = LoggerFactory.getLogger(AdminProductController.class);
    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req) {
        log.info("REST request to create product: {}", req.getTitle());
        log.debug("Create product payload details: {}", req);

        Product product = productService.createProduct(req);
        log.info("Successfully created product with ID: {}", product.getId());

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        log.info("REST request to delete product ID: {}", productId);

        productService.deleteProduct(productId);
        log.info("Successfully deleted product ID: {}", productId);

        ApiResponse res = new ApiResponse();
        res.setMessage("Product deleted successfully");
        res.setStatus(true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAllProducts() {
        log.info("REST request to fetch all admin products");

        List<Product> products = productService.findAllProducts();
        log.debug("Retrieved {} total products for admin", products.size());

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{productId}/update")
    public ResponseEntity<Product> updateProduct(
            @RequestBody Product req,
            @PathVariable Long productId) throws ProductException {
        log.info("REST request to update product ID: {}", productId);
        log.debug("Update product payload details for ID {}: {}", productId, req);

        Product product = productService.updateProduct(productId, req);
        log.info("Successfully updated product ID: {}", productId);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/creates")
    public ResponseEntity<ApiResponse> createMultipleProducts(
            @RequestBody CreateProductRequest[] req) {
        for (CreateProductRequest product : req) {
            log.debug("Creating multiple products: {}", product.getTitle());
            productService.createProduct(product);
        }

        log.info("Successfully created {} products", req);

        ApiResponse res = new ApiResponse();
        res.setMessage("Products created successfully");
        res.setStatus(true);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}