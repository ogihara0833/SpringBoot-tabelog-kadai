package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;

@Component
public class SignupEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public SignupEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishSignupEvent(User user, String requestUrl) {
        SignupEvent event = new SignupEvent(this, user, requestUrl);
        eventPublisher.publishEvent(event);
    }
}
