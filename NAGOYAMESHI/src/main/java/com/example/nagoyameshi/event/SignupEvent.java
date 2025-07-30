package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.User;

import lombok.Getter;

@Getter
public class SignupEvent extends ApplicationEvent {

    private final User user;
    private final String requestUrl;

    public SignupEvent(Object source, User user, String requestUrl) {
        super(source);
        this.user = user;
        this.requestUrl = requestUrl;
    }
}
