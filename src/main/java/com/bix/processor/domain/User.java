package com.bix.processor.domain;


import com.bix.processor.controller.domain.UserRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends CoreEntity {

    private String name;
    private String email;
    private String passwordHash;
    private Instant lastImageProcessed;
    private int processCount = 0;
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;

    public User(UserRequest userRequest) {
        this.email = userRequest.getEmail();
        this.name = userRequest.getName();
        this.subscriptionPlan = userRequest.getSubscriptionPlan();
    }
}