package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) throws UserException {
        log.info("REST request to fetch authenticated user profile");

        User user = userService.findUserProfileByJwt(jwt);
        log.info("Successfully retrieved user profile for userId: {}, email: {}",
                user != null ? user.getId() : "null",
                user != null ? user.getEmail() : "null");

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}