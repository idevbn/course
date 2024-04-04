package com.ead.course.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class SubscriptionDTO {

    @NotNull
    private UUID userId;

}
