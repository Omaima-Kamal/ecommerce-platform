package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.config.JwtProvider;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.repository.UserRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImplementation.class);

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserServiceImplementation(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public User findUserById(Long userId) throws UserException {
        log.info("Fetching user profile for userId: {}", userId);

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            log.debug("Successfully retrieved user with ID: {}", userId);
            return user.get();
        }

        log.warn("User lookup failed: No user found with ID: {}", userId);
        throw new UserException("User not found with id : " + userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserProfileByJwt(String jwt) throws UserException {
        log.info("Attempting to fetch user profile using JWT token");

        String email = jwtProvider.getEmailFromToken(jwt);
        log.debug("Extracted email from JWT: {}", email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("JWT profile fetch failed: No user found for email: {}", email);
            throw new UserException("User not found with email : " + email);
        }

        Hibernate.initialize(user.getAddress());

        log.info("Successfully fetched user profile and lazy-loaded details for userId: {}", user.getId());
        return user;
    }
}