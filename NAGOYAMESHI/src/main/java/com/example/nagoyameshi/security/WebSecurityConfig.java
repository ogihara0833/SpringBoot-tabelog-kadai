package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/signup/**","/signup","/reset-password/**", "/css/**", "/js/**", "/images/**","/storage/**", "/restaurants", "/restaurants/{id}","/restaurants/*/reviews/**").permitAll()

                .requestMatchers("/admin/**").hasRole("ADMIN")

                .requestMatchers("/premium/**","/restaurants/*/reviews/register", "/restaurants/*/reviews/create","/reservations/**").hasRole("PREMIUM")

                .anyRequest().authenticated()

            )
            .formLogin(form -> form
                .loginPage("/login")                
                .loginProcessingUrl("/login")       
                .defaultSuccessUrl("/?loggedIn", true)       
                .failureUrl("/login?error")         
                .permitAll()                        
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                     
                .logoutSuccessUrl("/?loggedOut")       
                .permitAll()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/stripe/webhook"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
