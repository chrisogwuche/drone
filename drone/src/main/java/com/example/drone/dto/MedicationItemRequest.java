package com.example.drone.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicationItemRequest {
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "name must contain Uppercase,Lowercase,_,-")
    @NotNull(message = "name required!")
    private String name;
    @NotNull(message = "weight required!")
    private String weight;
    @NotNull(message = "code required!")
    @Pattern(regexp = "^[A-Z0-9_]*$", message = "code must contain Uppercase,number and _")
    private String code;
    @NotNull(message = "image required!")
    private String imageUrl;
}
