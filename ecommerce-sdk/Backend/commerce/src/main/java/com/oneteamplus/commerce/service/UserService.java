package com.oneteamplus.commerce.service;


import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.User;

public interface UserService {

    public User findUserById(Long userId) throws UserException;
    public User findUserProfileByJwt(String jwt) throws UserException;



}
