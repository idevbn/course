package com.ead.course.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubscriptionDTO {

    @NotNull
    private UUID userId;

}
