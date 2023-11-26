package com.example.drone.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum State {
    IDLE,
    LOADING,
    LOADED,
    DELIVERING,
    DELIVERED,
    RETURNING;


}
