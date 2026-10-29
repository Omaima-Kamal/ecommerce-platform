package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.payload.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Product createProduct(CreateProductRequest request);
    public String deleteProduct(Long productId) throws ProductException;
    public Product updateProduct(Long productId, Product requsetProduct) throws ProductException;
    public Product findProductById(Long id) throws ProductException;
    public List<Product> findProductByCategory(String category);
    public Page<Product> getAllProducts(String topLevelCategory, String secondLevelCategory, String category, List<String> colors,
                                        List<String> sizes, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort,
                                        String stock, Integer pageNumber, Integer pageSize);
    public List<Product> findAllProducts();

}
