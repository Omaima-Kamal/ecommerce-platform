package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.config.JwtProvider;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Cart;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.LoginRequest;
import com.oneteamplus.commerce.payload.response.AuthResponse;
import com.oneteamplus.commerce.repository.UserRepository;
import com.oneteamplus.commerce.service.CartService;
import com.oneteamplus.commerce.service.CustomUserServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserServiceImplementation customUserService;
    private final CartService cartService;

    public AuthController(UserRepository userRepository, JwtProvider jwtProvider, PasswordEncoder passwordEncoder, CustomUserServiceImplementation customUserService, CartService cartService) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.customUserService = customUserService;
        this.cartService = cartService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {
        String email = user.getEmail();
        log.info("REST request to register new user with email: {}", email);

        User isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist != null) {
            log.warn("Signup failed: Email {} is already registered", email);
            throw new UserException("Email Is Already Used With Another Account");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setFirstName(user.getFirstName());
        createdUser.setLastName(user.getLastName());
        createdUser.setRole("ROLE_USER");
        createdUser.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(createdUser);
        log.info("User successfully created with ID: {}", savedUser.getId());

        Cart cart = cartService.createCart(savedUser);
        log.debug("Cart created for user ID: {} with cart ID: {}", savedUser.getId(), cart != null ? cart.getId() : "null");

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        log.info("JWT token generated successfully for new user ID: {}", savedUser.getId());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signup Success");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) throws UserException {
        String username = loginRequest.getEmail();
        log.info("REST request to authenticate user with email: {}", username);

        Authentication authentication = authenticate(username, loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        log.info("User authenticated successfully. Issued JWT for email: {}", username);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signin Success");

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        log.debug("Authenticating credentials for username: {}", username);

        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            log.warn("Authentication failed: User details not found for username: {}", username);
            throw new BadCredentialsException("Invalid Username...");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("Authentication failed: Password mismatch for username: {}", username);
            throw new BadCredentialsException("Invalid Password...");
        }

        log.debug("Password verification successful for username: {}", username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}