package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserServiceImplementation implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserServiceImplementation.class);

    private final UserRepository userRepository;

    public CustomUserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            log.warn("Authentication failed: no user found with email={}", username);
            throw new UsernameNotFoundException("user not found with email - " + username);
        }

        String role = user.getRole() != null ? user.getRole() : "ROLE_USER";
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        log.info("Loaded user for authentication: userId={} email={} role={}", user.getId(), user.getEmail(), role);

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}