package com.bix.processor.domain;


import com.bix.processor.controller.domain.UserRequest;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends CoreEntity {

    private String name;
    private String email;
    private String passwordHash;

    private SubscriptionPlan subscriptionPlan;

    public User(UserRequest userRequest) {
        this.email = userRequest.getEmail();
        this.name = userRequest.getName();
        this.subscriptionPlan = userRequest.getSubscriptionPlan();
    }
}