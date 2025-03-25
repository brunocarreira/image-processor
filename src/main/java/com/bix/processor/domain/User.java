package com.bix.processor.domain;


import com.bix.processor.controller.domain.UserRequest;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class User extends CoreEntity {

    private String name;
    private String email;
    private String passwordHash;
    private Instant lastImageProcessed;
    private int processCount = 0;

    private SubscriptionPlan subscriptionPlan;

    public User(UserRequest userRequest) {
        this.email = userRequest.getEmail();
        this.name = userRequest.getName();
        this.subscriptionPlan = userRequest.getSubscriptionPlan();
    }
}