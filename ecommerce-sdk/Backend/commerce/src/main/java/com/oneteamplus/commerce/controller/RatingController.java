package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Rating;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.RatingRequest;
import com.oneteamplus.commerce.service.RatingService;
import com.oneteamplus.commerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    private final UserService userService;
    private final RatingService ratingService;

    public RatingController(UserService userService, RatingService ratingService) {
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @PostMapping("/create")
    public ResponseEntity<Rating> createRating(@RequestBody RatingRequest req,
                                               @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        log.info("REST request to create rating for product ID: {}", req != null ? req.getProductId() : "null");
        log.debug("Rating request details - Rating value: {}", req != null ? req.getRating() : "null");

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating rating request for userId: {}", user.getId());

        Rating rating = ratingService.createRating(req, user);
        log.info("Successfully created rating with ID: {} for product ID: {} by userId: {}",
                rating != null ? rating.getId() : "null",
                req != null ? req.getProductId() : "null",
                user.getId());

        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getProductsRating(@PathVariable Long productId,
                                                          @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        log.info("REST request to fetch ratings for product ID: {}", productId);

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating product ratings request for userId: {}", user.getId());

        List<Rating> ratings = ratingService.getProductsRating(productId);
        log.info("Retrieved {} ratings for product ID: {} (requested by userId: {})", ratings.size(), productId, user.getId());

        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }
}