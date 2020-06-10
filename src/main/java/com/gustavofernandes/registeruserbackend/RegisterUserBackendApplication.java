package com.gustavofernandes.registeruserbackend;

import com.gustavofernandes.registeruserbackend.config.property.RegisterUserProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableConfigurationProperties(RegisterUserProperty.class)
@EnableAsync
public class RegisterUserBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegisterUserBackendApplication.class, args);
        //System.out.println(new BCryptPasswordEncoder().encode("12345678"));
    }

    @Bean(name = "fileExecutor")
    public Executor asyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.initialize();
        return executor;
    }

}
