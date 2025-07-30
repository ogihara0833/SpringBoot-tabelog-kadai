package com.example.nagoyameshi.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class MembershipService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public MembershipService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void updateToPremium(Integer userId, String subscriptionId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPremium(true);
            user.setStripeSubscriptionId(subscriptionId);
            user.setRole(roleRepository.findByName("PREMIUM"));
            userRepository.save(user);
        }
    }

    public void upgradeToPremium(User user, String subscriptionId) {
        updateToPremium(user.getId(), subscriptionId);
    }
    
    @Transactional
    public void downgradeToFree(User user) {
        Role freeRole = roleRepository.findByName("FREE");
        user.setRole(freeRole);
        user.setPremium(false);
        user.setStripeSubscriptionId(null); 
        user.setStripeCustomerId(null);
        userRepository.save(user);
    }
}
