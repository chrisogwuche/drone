package com.example.drone.repository;

import com.example.drone.entity.MedicationItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationItemRepository extends JpaRepository<MedicationItems,Long> {
}
