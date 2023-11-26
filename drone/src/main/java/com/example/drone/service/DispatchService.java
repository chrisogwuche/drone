package com.example.drone.service;

import com.example.drone.dto.MedicationItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DispatchService {

    ResponseEntity<String> dispatch(MedicationItemRequest request);
}
