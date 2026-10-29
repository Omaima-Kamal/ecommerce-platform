package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Review;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.ReviewRequest;
import com.oneteamplus.commerce.service.ReviewService;
import com.oneteamplus.commerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final UserService userService;
    private final ReviewService reviewService;

    public ReviewController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest req,
                                               @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        Long productId = (req != null) ? req.getProductId() : null;
        log.info("REST request to create review for product ID: {}", productId);

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating review creation request for userId: {}", user.getId());

        Review review = reviewService.createReview(req, user);
        log.info("Successfully created review with ID: {} for product ID: {} by userId: {}",
                review != null ? review.getId() : "null", productId, user.getId());

        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductsReview(@PathVariable Long productId) throws UserException, ProductException {
        log.info("REST request to fetch reviews for product ID: {}", productId);

        List<Review> reviews = reviewService.getAllReview(productId);
        log.info("Successfully retrieved {} reviews for product ID: {}", reviews.size(), productId);

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}