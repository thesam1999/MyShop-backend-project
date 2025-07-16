package com.sideproject.myshop.exceptions;

import com.sideproject.myshop.dto.ExceptionResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestControllerAdvice  // 監聽全域 Controller 裡的例外（Exception）
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundEx.class) //專門處理這種類型的例外
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFoundEx ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .error("Resource Not Found in database")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")))
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentFailed(PaymentFailedException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .error("Payment Failed")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")))
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ExceptionResponse> handleRequestValidationException(InternalServerException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .error("something went wrong")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .error("Invalid argument")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")))
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(BadRequestException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .error("Bad request")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")))
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 遺漏的Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnknownException(Exception ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .error("Internal Server Error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // 可加入其他錯誤處理，例如：
    // @ExceptionHandler(InvalidRequestEx.class)
    // @ExceptionHandler(MethodArgumentNotValidException.class)
}
