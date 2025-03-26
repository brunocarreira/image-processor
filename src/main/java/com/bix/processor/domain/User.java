package com.bix.processor.domain;


import com.bix.processor.controller.domain.UserRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "TB_USER")
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