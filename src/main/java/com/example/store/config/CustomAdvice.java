package com.example.store.config;

import com.example.store.exception.NotFoundException;
import com.example.store.model.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class CustomAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> recordNotFound(NotFoundException ex) {
        log.debug("BadRequest_badRequest : " + ex.getMessage());

        ExceptionResponse response = new ExceptionResponse();
        response.setStatus("Bad Request");
        response.setRequestId(MDC.get("requestId"));
        response.setMessage(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
