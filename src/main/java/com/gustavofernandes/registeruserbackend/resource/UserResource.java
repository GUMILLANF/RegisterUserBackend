package com.gustavofernandes.registeruserbackend.resource;

import com.gustavofernandes.registeruserbackend.model.User;
import com.gustavofernandes.registeruserbackend.repository.UserRepository;
import com.gustavofernandes.registeruserbackend.service.UserService;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/validation/{login}")
    public ResponseEntity<User> validate(@PathVariable String login) {
        User updatedUser = userService.validate(login);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/{login}")
    public ResponseEntity<User> searchByLogin(@PathVariable String login) {
        Optional<User> user = userRepository.findByLogin(login);
        return (user.isPresent() ? ResponseEntity.ok(user.get()) : ResponseEntity.notFound().build());
    }

    @PutMapping("/resendemail/{id}")
    public ResponseEntity<User> resendEmail(@PathVariable Long id, @Valid @RequestBody User user)
            throws InvalidEmailException {
        final User savedUser = userService.edit(id, user);
        try {
            userService.triggerEmail(savedUser);
        } catch (Exception e) {
            userService.updateUnsetEmail(savedUser);
        }
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

}
