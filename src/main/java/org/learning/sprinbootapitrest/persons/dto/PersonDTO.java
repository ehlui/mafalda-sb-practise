package org.learning.sprinbootapitrest.persons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    @Pattern(regexp = "^[a-zA-Z]+${2,32}", message = "{person.dto.name.pattern}")
    private String name;
    @NotNull(message = "{person.dto.age.empty}")
    //@Min(value = 0, message = "{person.dto.age.negative}")
    @Positive(message =  "{person.dto.age.negative}")
    private Integer age;
}
