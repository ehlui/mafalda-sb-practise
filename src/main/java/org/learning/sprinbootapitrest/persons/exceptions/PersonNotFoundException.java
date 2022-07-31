package org.learning.sprinbootapitrest.persons.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(int id) {
        super("Person with id '%d' cannot be found! It may not exists.".formatted(id));
    }
}
