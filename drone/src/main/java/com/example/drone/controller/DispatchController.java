package com.example.drone.controller;

import com.example.drone.dto.MedicationItemRequest;
import com.example.drone.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drone")
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping("/dispatch")
    public ResponseEntity<String> dispatch(@Valid @RequestBody MedicationItemRequest request){
        return dispatchService.dispatch(request);
    }
}
