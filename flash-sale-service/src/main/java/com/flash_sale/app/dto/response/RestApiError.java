package com.flash_sale.app.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RestApiError {
    ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "ERROR NOT FOUND"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL SERVER ERROR");

    private final int code;
    private final String message;
}