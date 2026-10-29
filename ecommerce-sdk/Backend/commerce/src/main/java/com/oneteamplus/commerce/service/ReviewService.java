package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Review;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.ReviewRequest;

import java.util.List;

public interface ReviewService {

    public Review createReview(ReviewRequest req, User user)throws ProductException;

    public List<Review> getAllReview(Long productId);
}
