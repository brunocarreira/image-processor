package com.bix.processor.controller.domain;

import com.bix.processor.domain.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String name;
    @NotNull
    @Email
    private String email;
    private String password;

    private SubscriptionPlan subscriptionPlan;
}