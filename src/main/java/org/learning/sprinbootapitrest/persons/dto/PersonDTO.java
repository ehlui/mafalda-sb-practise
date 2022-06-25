package org.learning.sprinbootapitrest.persons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    @NotEmpty(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Age is mandatory")
    @Min(value = 0, message = "Negative age is wrong.")
    private Integer age;
}
