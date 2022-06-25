package org.learning.sprinbootapitrest.persons.handlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.learning.sprinbootapitrest.persons.errors.ErrorApi;
import org.learning.sprinbootapitrest.persons.errors.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Custom exception handlers for our PersonController
 *
 * @see org.learning.sprinbootapitrest.persons.PersonController
 */
public class PersonExceptionHandlers {

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorApi> HandlerPersonNotFound(PersonNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorApi.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> HandleValidationExceptions(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorApi.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(exception.getMessage())
                        .build());
    }
}
