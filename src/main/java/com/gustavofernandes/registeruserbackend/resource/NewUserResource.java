package com.gustavofernandes.registeruserbackend.resource;

import com.gustavofernandes.registeruserbackend.model.User;
import com.gustavofernandes.registeruserbackend.service.UserService;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidEmailException;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidTokenException;
import com.gustavofernandes.registeruserbackend.service.exception.LoginUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/newuser")
public class NewUserResource {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> saveUser(@Valid @RequestBody User user)
            throws LoginUnavailableException, InvalidEmailException {
        final User savedUser = userService.save(user);
        try {
            userService.triggerEmail(savedUser);
        } catch (Exception e) {
            userService.updateUnsetEmail(savedUser);
        }
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

}
