package com.gustavofernandes.registeruserbackend.service;

import com.gustavofernandes.registeruserbackend.model.User;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidEmailException;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidTokenException;
import com.gustavofernandes.registeruserbackend.service.exception.LoginUnavailableException;

public interface UserService {

    User save(User user) throws LoginUnavailableException, InvalidEmailException;
    User edit(Long id, User user) throws InvalidEmailException;
    void updateUnsetEmail(User user);
    User validate(String login);
    void triggerEmail(User user);

}
