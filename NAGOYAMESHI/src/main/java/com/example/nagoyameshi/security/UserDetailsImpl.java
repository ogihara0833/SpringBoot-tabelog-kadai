package com.example.nagoyameshi.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.nagoyameshi.entity.User;

public class UserDetailsImpl implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + user.getRole().getName();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword(); 
    }

    @Override
    public String getUsername() {
        return user.getEmail(); 
    }

    @Override
    public boolean isAccountNonExpired() {


        return true; // アカウント期限なし
    }

    @Override
    public boolean isAccountNonLocked() {
 

        return true; // ロックなし
    }

    @Override
    public boolean isCredentialsNonExpired() {
 

        return true; // パスワード期限なし
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled(); 
    }

    public User getUser() {
        return user; // エンティティ参照用
    }
}
