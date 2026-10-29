package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Category;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.payload.request.CreateProductRequest;
import com.oneteamplus.commerce.repository.CategoryRepository;
import com.oneteamplus.commerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplementation implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImplementation.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final CategoryService categoryService;

    public ProductServiceImplementation(ProductRepository productRepository, CategoryRepository categoryRepository, FileStorageService fileStorageService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        Category topLevel = categoryService.findOrCreateTopLevel(request.getTopLevelCategory());
        Category secondLevel = categoryService.findOrCreateSecondLevel(request.getSecondLevelCategory(), topLevel);
        Category thirdLevel = categoryService.findOrCreateThirdLevel(request.getThirdLevelCategory(), secondLevel);

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setColor(request.getColor());
        product.setDescription(request.getDescription());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setDiscountPercent(request.getDiscountPercent());

        String localImageUrl = fileStorageService.downloadAndStore(request.getImageUrl());
        product.setImageUrl(localImageUrl);

        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setSizes(request.getSizes());
        product.setQuantity(request.getQuantity());
        product.setCategory(thirdLevel);
        product.setCreateAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        log.info("Created product id={} title='{}' category={} imageUrl={}",
                savedProduct.getId(), savedProduct.getTitle(), thirdLevel.getName(), localImageUrl);

        return savedProduct;
    }

    @Override
    @Transactional
    public String deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        productRepository.delete(product);
        log.info("Deleted product id={}", productId);
        return "Product deleted Successfully";
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, Product requestProduct) throws ProductException {
        Product product = findProductById(productId);

        if (requestProduct.getQuantity() != 0) {
            log.info("Updating product id={} quantity: {} -> {}", productId, product.getQuantity(), requestProduct.getQuantity());
            product.setQuantity(requestProduct.getQuantity());
        }

        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Product findProductById(Long id) throws ProductException {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found id={}", id);
                    return new ProductException(
                            "Product not found with id - " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductByCategory(String category) {
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String topLevelCategory, String secondLevelCategory, String category,
                                        List<String> colors, List<String> sizes, Integer minPrice, Integer maxPrice,
                                        Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {


        Sort productSort = resolveSort(sort);
        Pageable pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize != null ? pageSize : 10, productSort);

        boolean categoryWasRequested = category != null && !category.isEmpty();
        Long categoryId = categoryWasRequested ? resolveCategoryId(topLevelCategory, secondLevelCategory, category) : null;

        if (categoryWasRequested && categoryId == null) {
            log.warn("Category path not found: top={} second={} leaf={} — returning empty result set",
                    topLevelCategory, secondLevelCategory, category);
            return new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        log.info("Fetching products: topLevel={} secondLevel={} category={} resolvedCategoryId={} colors={} sizes={} minPrice={} maxPrice={} minDiscount={} stock={} page={}/{}",
                topLevelCategory, secondLevelCategory, category, categoryId, colors, sizes, minPrice, maxPrice, minDiscount, stock, pageNumber, pageSize);

        List<String> normalizedColors = (colors == null || colors.isEmpty())
                ? Collections.emptyList()
                : colors.stream().filter(c -> c != null && !c.isBlank()).map(String::toLowerCase).collect(Collectors.toList());
        boolean colorsEmpty = normalizedColors.isEmpty();

        List<String> normalizedSizes = (sizes == null || sizes.isEmpty())
                ? Collections.emptyList()
                : sizes.stream().filter(s -> s != null && !s.isBlank()).map(String::toLowerCase).collect(Collectors.toList());
        boolean sizesEmpty = normalizedSizes.isEmpty();

        Integer minDiscountVal = (minDiscount == null || minDiscount <= 0) ? null : minDiscount;

        return productRepository.filterProducts(categoryId, minPrice, maxPrice, minDiscountVal, stock,
                normalizedColors, colorsEmpty, normalizedSizes, sizesEmpty, pageable);
    }

    private Sort resolveSort(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.ASC, "discountedPrice");
        }
        return switch (sort) {
            case "price_high" -> Sort.by(Sort.Direction.DESC, "discountedPrice");
            case "price_low" -> Sort.by(Sort.Direction.ASC, "discountedPrice");
            default -> Sort.by(Sort.Direction.ASC, "discountedPrice");
        };
    }

    private Long resolveCategoryId(String topLevelCategory, String secondLevelCategory, String category) {
        if (category == null || category.isEmpty()) {
            return null;
        }

        if (topLevelCategory == null || secondLevelCategory == null) {
            Category leaf = categoryRepository.findByNameIgnoreCase(category);
            return leaf != null ? leaf.getId() : null;
        }

        Category top = categoryRepository.findByNameIgnoreCase(topLevelCategory);
        if (top == null) {
            log.warn("No top-level category found matching '{}'", topLevelCategory);
            return null;
        }

        Category second = categoryRepository.findByNameIgnoreCaseAndParentCategoryId(secondLevelCategory, top.getId());
        if (second == null) {
            log.warn("No second-level category '{}' found under top-level '{}'", secondLevelCategory, top.getName());
            return null;
        }

        Category leaf = categoryRepository.findByNameIgnoreCaseAndParentCategoryId(category, second.getId());
        if (leaf == null) {
            log.warn("No category '{}' found under '{}' > '{}'", category, top.getName(), second.getName());
        }
        return leaf != null ? leaf.getId() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
}