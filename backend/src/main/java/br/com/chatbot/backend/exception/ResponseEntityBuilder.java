package br.com.chatbot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityBuilder {
    private ResponseEntityBuilder() {}

    public static ResponseEntity<Object> build(ErrorMessage errorMessage) {
        return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
    }
}