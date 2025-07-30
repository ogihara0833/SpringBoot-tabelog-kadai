package com.example.nagoyameshi.config; 

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository; 

@Component
public class PasswordAutoEncoder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordAutoEncoder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String password = user.getPassword();

            
            if (!password.startsWith("$2a$")) {
                
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }
    }
}
