package com.gustavofernandes.registeruserbackend.security;

import com.gustavofernandes.registeruserbackend.ApplicationContextLoad;
import com.gustavofernandes.registeruserbackend.model.User;
import com.gustavofernandes.registeruserbackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@Component
public class JWTTokenAuthenticationService {

    private static final long EXPIRATION_TIME = 600000;
    private static final String SECRET = "1a2b3c4d5e@";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    public void addAuthentication(HttpServletResponse response, String username) throws IOException {
        String JWT = returnJWT(username);

        String token = TOKEN_PREFIX + " " + JWT;

        response.addHeader(HEADER_STRING, token);

        response.getWriter().write("{\"Authorization\": \""+token+"\"}");
    }

    public String returnJWT(String username) throws IOException {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader(HEADER_STRING);

        Authentication auth = getAuthentication(token, response);

        return auth;
    }

    public Authentication getAuthentication(String token, HttpServletResponse response) {
        try {
            if (token != null) {

                String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

                String user = Jwts.parser().setSigningKey(SECRET)
                        .parseClaimsJws(tokenLimpo)
                        .getBody().getSubject();
                if (user != null) {

                    Optional<User> userOpt = ApplicationContextLoad.getApplicationContext()
                            .getBean(UserRepository.class).findByLogin(user);

                    if (userOpt.isPresent()) {
                        return new UsernamePasswordAuthenticationToken(
                                userOpt.get().getLogin(),
                                userOpt.get().getPassw(),
                                userOpt.get().getAuthorities());
                    }
                }

            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return null;
        }

        return null;
    }

}
