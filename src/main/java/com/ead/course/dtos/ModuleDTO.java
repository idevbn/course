package com.ead.course.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModuleDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

}