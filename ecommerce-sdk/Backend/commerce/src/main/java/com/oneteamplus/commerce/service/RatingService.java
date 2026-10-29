package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Rating;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.RatingRequest;

import java.util.List;

public interface RatingService {

    public Rating createRating(RatingRequest req, User user) throws ProductException;
    public List<Rating> getProductsRating(Long productId);
}
