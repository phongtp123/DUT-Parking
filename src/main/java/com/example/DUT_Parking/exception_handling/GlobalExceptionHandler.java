package com.example.DUT_Parking.exception_handling;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.DUT_Parking.respond.APIRespond;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<APIRespond> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        APIRespond apiResponse = new APIRespond();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIRespond> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        APIRespond apiResponse = new APIRespond();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<APIRespond> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(APIRespond.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIRespond> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {

        }

        APIRespond apiResponse = new APIRespond();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}