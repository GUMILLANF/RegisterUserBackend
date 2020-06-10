package com.gustavofernandes.registeruserbackend.service.implementation;

import com.gustavofernandes.registeruserbackend.ApplicationContextLoad;
import com.gustavofernandes.registeruserbackend.config.property.RegisterUserProperty;
import com.gustavofernandes.registeruserbackend.mail.Mailer;
import com.gustavofernandes.registeruserbackend.model.Enum.Status;
import com.gustavofernandes.registeruserbackend.model.User;
import com.gustavofernandes.registeruserbackend.repository.UserRepository;
import com.gustavofernandes.registeruserbackend.security.JWTTokenAuthenticationService;
import com.gustavofernandes.registeruserbackend.service.UserService;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidEmailException;
import com.gustavofernandes.registeruserbackend.service.exception.InvalidTokenException;
import com.gustavofernandes.registeruserbackend.service.exception.LoginUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImplementation implements UserService {

    private static final String urlBase = "http://localhost:4200/validating/";
    private static final String subject = "Validação de Usuário";

    private final UserRepository userRepository;

    @Autowired
    private Mailer mailer;

    @Autowired
    private RegisterUserProperty property;


    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) throws LoginUnavailableException, InvalidEmailException {
        loginValidation(user);
        emailValidation(user);
        String encrypted = new BCryptPasswordEncoder().encode(user.getPassw());
        user.setPassw(encrypted);
        user.setStatus(Status.AGUARDANDO_VALIDACAO);
        user.setRegistrationDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User edit(Long id, User user) throws InvalidEmailException {
        Boolean changed = false;
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new EmptyResultDataAccessException(1);
        }
        User userSaved = optionalUser.get();

        if (!userSaved.getEmail().equals(user.getEmail())) {
            userSaved.setEmail(user.getEmail());
            emailValidation(userSaved);
            changed = true;
        }

        if (!userSaved.getStatus().equals(Status.AGUARDANDO_VALIDACAO)) {
            userSaved.setStatus(Status.AGUARDANDO_VALIDACAO);
            changed = true;

        }

        return (changed ? userRepository.save(userSaved) : userSaved);
    }

    @Override
    public void updateUnsetEmail(User user) {
        user.setStatus(Status.EMAIL_NAO_ENVIADO);
        userRepository.save(user);
    }

    @Async("fileExecutor")
    @Override
    public void triggerEmail(User user) {
        try {
            String JWT = new JWTTokenAuthenticationService().returnJWT(user.getLogin());

            String message = "<a href=\"" + urlBase + JWT + "\">Click aqui para ativar seu usuário.</a>";

            List<String> recipients = new ArrayList<>();
            recipients.add(user.getEmail());

            mailer.sendEmail(property.getMail().getUserName(), recipients, subject, message);
        } catch (Exception e) {

        }
    }

    @Override
    public User validate(String login) {
        Optional<User> userOpt = ApplicationContextLoad.getApplicationContext()
                .getBean(UserRepository.class).findByLogin(login);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(Status.VALIDADO);
            user.setValidationDate(LocalDateTime.now());
            return userRepository.save(user);
        }
        return null;
    }

    private void loginValidation(User user) throws LoginUnavailableException {
        Optional<User> optional = userRepository.findByLogin(user.getLogin());

        if (optional.isPresent()) {
            throw new LoginUnavailableException("Login '" + user.getUsername().toUpperCase() + "' não está disponível!");
        }
    }

    private void emailValidation(User user) throws InvalidEmailException {
        Optional<User> optional = userRepository.findByEmail(user.getEmail());

        if (optional.isPresent()) {
            throw new InvalidEmailException("Email '" + user.getEmail().toLowerCase() + "' já existe!");
        }

        if (!isValidEmailAddressRegex(user.getEmail())) {
            throw new InvalidEmailException(user.getEmail().toLowerCase() + " é um email inválido!");
        }
    }

    private static Boolean isValidEmailAddressRegex(String email) {
        Boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }

}
