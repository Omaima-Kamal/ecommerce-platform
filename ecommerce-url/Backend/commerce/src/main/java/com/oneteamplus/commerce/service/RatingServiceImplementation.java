package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.model.Rating;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.RatingRequest;
import com.oneteamplus.commerce.repository.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class RatingServiceImplementation implements RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingServiceImplementation.class);

    private final RatingRepository ratingRepository;
    private final ProductService productService;

    public RatingServiceImplementation(RatingRepository ratingRepository, ProductService productService) {
        this.ratingRepository = ratingRepository;
        this.productService = productService;
    }

    @Override
    public Rating createRating(RatingRequest req, User user) throws ProductException {

        log.info("Creating rating for productId: {} by userId: {}", req.getProductId(), user != null ? user.getId() : "null");
        log.debug("Rating request details - Rating: {}", req.getRating());

        Product product = productService.findProductById(req.getProductId());

        Rating rating = new Rating();
        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(req.getRating());
        rating.setCratedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);
        log.info("Successfully created rating with ID: {} for productId: {}", savedRating.getId(), req.getProductId());

        return savedRating;
    }

    @Override
    public List<Rating> getProductsRating(Long productId) {
        log.info("Fetching ratings for productId: {}", productId);

        List<Rating> ratings = ratingRepository.getAllProductsRating(productId);
        log.debug("Retrieved {} ratings for productId: {}", ratings.size(), productId);

        return ratings;
    }
}