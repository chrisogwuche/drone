package com.example.drone.repository;

import com.example.drone.entity.Drone;
import com.example.drone.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<Drone,Long> {
    @Query(value = "select d from Drone d left join d.medicationItemsList where d.state = ?1 or d.state = ?2 and d.batteryLevel >= ?3")
    Optional<List<Drone>> findAvailableDrones(State state1, State state2, Integer batteryLevel);

    //List<Drone> findAllByStateOrStateAndBatteryLevelGreaterThanEqual(State state1,State state2, Integer batterLevel);
    List<Drone> findByState(State state);
}
