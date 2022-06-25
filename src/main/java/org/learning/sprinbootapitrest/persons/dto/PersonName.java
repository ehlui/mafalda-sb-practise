package org.learning.sprinbootapitrest.persons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonName {
    @NotEmpty(message = "Name is mandatory")
    private String name;
}
