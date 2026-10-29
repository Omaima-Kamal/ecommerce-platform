package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.model.Review;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.ReviewRequest;
import com.oneteamplus.commerce.repository.ProductRepository;
import com.oneteamplus.commerce.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImplementation implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImplementation.class);

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ReviewServiceImplementation(ReviewRepository reviewRepository, ProductService productService, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    public Review createReview(ReviewRequest req, User user) throws ProductException {
        Long userId = (user != null) ? user.getId() : null;
        log.info("Creating review for productId: {} by userId: {}", req.getProductId(), userId);
        log.debug("Review payload content: {}", req.getReview());

        Product product = productService.findProductById(req.getProductId());

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview(req.getReview());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        log.info("Successfully created review with ID: {} for productId: {}", savedReview.getId(), req.getProductId());

        return savedReview;
    }

    @Override
    public List<Review> getAllReview(Long productId) {
        log.info("Fetching all reviews for productId: {}", productId);

        List<Review> reviews = reviewRepository.getAllProductsReview(productId);
        log.debug("Retrieved {} reviews for productId: {}", reviews.size(), productId);

        return reviews;
    }
}