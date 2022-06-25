package org.learning.sprinbootapitrest.persons.handlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.learning.sprinbootapitrest.persons.errors.ErrorApi;
import org.learning.sprinbootapitrest.persons.errors.PersonNotFoundException;
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
                .body(new ErrorApi(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> HandleValidationExceptions(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorApi(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }
}
