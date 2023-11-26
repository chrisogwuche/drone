package com.example.drone.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "medication_items")
public class MedicationItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double medWeight;
    private String code;
    private String imageUrl;
    @ManyToOne
    @JsonBackReference
    private Drone drone;
}
