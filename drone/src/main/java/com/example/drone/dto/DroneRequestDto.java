package com.example.drone.dto;

import com.example.drone.enums.Model;
import com.example.drone.enums.State;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DroneRequestDto{
        @Max(100)
        @NotNull(message = "serial number must not be null")
        private String serialNumber;
        @Enumerated(EnumType.STRING)
        private Model model;
        @Max(500)
        private Double weight;
        @Enumerated(EnumType.STRING)
        private State state;
        private Double maxWeight;
        private Integer batteryLevel;
}
