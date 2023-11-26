package com.example.drone.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errorHandler = new HashMap<>();
        e.getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errorHandler.put(fieldName,message);
        });

        return new ResponseEntity<>(errorHandler, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception e, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse();
        error.setErrorMessage(e.getMessage());
        error.setUrl(request.getRequestURI());
        error.setTime(LocalDateTime.now());
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> genericExceptions(Exception e, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse();
        error.setErrorMessage(e.getMessage());
        error.setUrl(request.getRequestURI());
        error.setTime(LocalDateTime.now());
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
