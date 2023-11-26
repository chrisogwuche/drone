package com.example.drone.entity;

import com.example.drone.enums.Model;
import com.example.drone.enums.State;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "drone")
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serialNumber;
    @Enumerated(EnumType.STRING)
    private Model model;
    private Double weight;
    @Enumerated(EnumType.STRING)
    private State state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer batteryLevel;
    private Double maxWeight;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.EAGER)
    List<MedicationItems> medicationItemsList;

    public void addToMedicationItemsList(@NonNull MedicationItems items){
        this.medicationItemsList.add(items);
    }

    public void addWeight(@NonNull Double weight){
        this.weight += weight;
    }
}
