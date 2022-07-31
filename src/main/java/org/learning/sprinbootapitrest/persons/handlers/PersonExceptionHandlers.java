package org.learning.sprinbootapitrest.persons.handlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.learning.sprinbootapitrest.persons.PersonController;
import org.learning.sprinbootapitrest.persons.exceptions.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Custom exception handlers for our PersonController
 *
 * @see org.learning.sprinbootapitrest.persons.PersonController
 */
@ControllerAdvice(assignableTypes = PersonController.class)
public class PersonExceptionHandlers {

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ApiException> HandlerPersonNotFound(PersonNotFoundException exception) {
        return buildResponseError(HttpStatus.NOT_FOUND,
                "Person not found exception",
                List.of(exception.getMessage()));
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ApiException> HandlerPersonNotFound(JsonMappingException exception) {
        List<String> details = exception.getPath().stream()
                .sequential()
                .map(JsonMappingException.Reference::getFieldName)
                .toList();

        return buildResponseError(HttpStatus.BAD_REQUEST, "Person properties with invalid type", details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getAllErrors().stream()
                .sequential()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return buildResponseError(HttpStatus.BAD_REQUEST, "Validation Failed Exception ", details);
    }

    private ResponseEntity<ApiException> buildResponseError(HttpStatus status,
                                                            String message,
                                                            List<String> details) {
        ApiException apiException = ApiException.builder()
                .message(message)
                .details(details)
                .status(status)
                .build();

        return ResponseEntity
                .status(status)
                .body(apiException);
    }

}
