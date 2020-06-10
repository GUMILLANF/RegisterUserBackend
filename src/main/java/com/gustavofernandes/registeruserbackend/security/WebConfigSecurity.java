package com.gustavofernandes.registeruserbackend.security;

import com.gustavofernandes.registeruserbackend.service.implementation.UserDatailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDatailsServiceImplementation userDatailsServiceImplementation;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

        .disable().authorizeRequests().antMatchers("/newuser").permitAll()
        .antMatchers("/newuser/").permitAll()
        .antMatchers("/newuser/**").permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
        UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new JWTApiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDatailsServiceImplementation)
        .passwordEncoder(new BCryptPasswordEncoder());
    }
}
