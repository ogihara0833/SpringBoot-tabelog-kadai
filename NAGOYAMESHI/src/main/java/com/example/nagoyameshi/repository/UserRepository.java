package com.example.nagoyameshi.repository;



import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findByEmail(String email);

    public Page<User> findByNameLikeOrEmailLike(String nameKeyword, String emailKeyword, Pageable pageable);

    Optional<User> findByStripeSubscriptionId(String subscriptionId);
    }
