package com.example.DUT_Parking.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key!", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email existed!", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1003, "Email is invalid , the format of a valid email is :abcd1234@gmail.com ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least 18", HttpStatus.BAD_REQUEST),
    INVALID_NAME(1009, "Họ và tên không được bỏ trống", HttpStatus.BAD_REQUEST),
    INVALID_GENDER(1010, "Giới tính không được bỏ trống", HttpStatus.BAD_REQUEST),
    INVALID_DIACHI(1011, "Địa chỉ không được bỏ trống", HttpStatus.BAD_REQUEST),
    INVALID_QUEQUAN(1012, "Quê quán không được bỏ trống", HttpStatus.BAD_REQUEST),
    INVALID_SDT(1013, "Số điện thoại không được bỏ trống", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS(1014, "Insufficient funds , your fund are not enough to buy this ticket", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
