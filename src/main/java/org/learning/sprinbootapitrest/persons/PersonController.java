package org.learning.sprinbootapitrest.persons;

import lombok.RequiredArgsConstructor;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;
import org.learning.sprinbootapitrest.persons.dto.PersonName;
import org.learning.sprinbootapitrest.persons.errors.PersonNotFoundException;
import org.learning.sprinbootapitrest.persons.handlers.PersonExceptionHandlers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PersonController extends PersonExceptionHandlers {
    private final PersonRepository personRepository;

    @GetMapping("persons")
    public ResponseEntity<?> getPersons() {
        List<Person> personsList = personRepository.getAll();
        return personsList.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(personsList);
    }

    @GetMapping("persons/name/{name}")
    public ResponseEntity<?> getPersonsByMatchingName(@PathVariable String name) {
        List<PersonDTO> personsDTOList = personRepository.findByName(name);
        return personsDTOList.size() != 0 ?
                ResponseEntity.ok(personsDTOList) :
                ResponseEntity.noContent().build();
    }

    @GetMapping("persons/{id}")
    public PersonDTO getPerson(@PathVariable Integer id) throws PersonNotFoundException {
        return personRepository.findById(id);
    }

    @PostMapping("persons")
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonDTO person) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(personRepository.save(person));
    }

    @DeleteMapping("persons/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable int id) {
        personRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("persons/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable int id, @Valid @RequestBody PersonDTO person) {
        System.out.println(person);
        personRepository.save(person, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("persons/{id}")
    public ResponseEntity<?> patchPerson(@PathVariable int id, @Valid @RequestBody PersonName personName) {
        personRepository.save(personName, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
