package com.product.app.controller.advice;

import com.product.app.dto.response.RestApiError;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.utility.CoreThrowHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice

public class ControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<RestApiResponse<Void>> handleValidationException(MethodArgumentNotValidException methodArgumentNotValidException){
        RestApiResponse<Void> apiResponse = new RestApiResponse<>();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.name());

        List<String> errorMessage = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        apiResponse.setMessage("Validation Failed");
        apiResponse.setError(errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler({CoreThrowHandler.class})
    public ResponseEntity<RestApiResponse<Void>> handlerCoreException(CoreThrowHandler coreThrowHandler){
        RestApiResponse<Void> apiResponse = new RestApiResponse<>();
        apiResponse.setCode(HttpStatus.valueOf(coreThrowHandler.getCode()).name());
        apiResponse.setMessage(coreThrowHandler.getMessage());
        apiResponse.setError((List<String>) coreThrowHandler.getError());
        return ResponseEntity.status(coreThrowHandler.getCode()).body(apiResponse);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<RestApiResponse<Void>> handlerCoreException(IllegalStateException illegalStateException){
        RestApiResponse<Void> apiResponse = new RestApiResponse<>();
        apiResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.name());
        apiResponse.setMessage(RestApiError.INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("code", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


}

