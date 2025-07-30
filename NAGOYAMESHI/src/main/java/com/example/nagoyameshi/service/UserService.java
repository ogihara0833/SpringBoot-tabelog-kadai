package com.example.nagoyameshi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.constant.MembershipType;
import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Transactional
    public User create(SignupForm form) {
        User user = new User();
        user.setName(form.getName());
        user.setPhoneNumber(form.getPhoneNumber());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEnabled(false);

        String roleName = form.getMembershipType() == MembershipType.PREMIUM ? "PREMIUM" : "FREE";
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);

        return userRepository.save(user);
    }

    @Transactional
    public void enable(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

   
    public User update(User user) {
        return userRepository.save(user);
    }

    public void updatePassword(Integer userId, String encodedPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPassword(encodedPassword);
            userRepository.save(user);
        });
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public boolean isSamePassword(String password, String confirmation) {
        return password.equals(confirmation);
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }
    
    @Transactional
    public void update(UserEditForm form) {
        User user = userRepository.getReferenceById(form.getId());

        user.setName(form.getName());
        user.setPhoneNumber(form.getPhoneNumber());
        user.setEmail(form.getEmail());

        userRepository.save(user);
    }
    
    public boolean isEmailChanged(UserEditForm form) {
        User user = userRepository.getReferenceById(form.getId());
        return !user.getEmail().equals(form.getEmail());
    }

}
