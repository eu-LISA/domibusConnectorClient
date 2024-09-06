/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.rest.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * The RestExceptionHandler class is a controller advice class that handles and responds to
 * exceptions thrown during RESTful API requests.
 *
 * @see ControllerAdvice
 * @see ResponseEntityExceptionHandler
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    // other exception handlers
    @ExceptionHandler(MessageNotFoundException.class)
    protected ResponseEntity<Object> handleMessageNotFound(
        MessageNotFoundException ex) {
        return buildResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ParameterException.class)
    protected ResponseEntity<Object> handleParameter(ParameterException ex) {
        return buildResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    protected ResponseEntity<Object> handleStorage(StorageException ex) {
        return buildResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MessageSubmissionException.class)
    protected ResponseEntity<Object> handleMessageSubmission(MessageSubmissionException ex) {
        return buildResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildResponseEntity(String apiError, HttpStatus status) {
        return new ResponseEntity<>(apiError, status);
    }
}

