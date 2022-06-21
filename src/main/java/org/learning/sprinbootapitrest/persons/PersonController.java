package org.learning.sprinbootapitrest.persons;

import lombok.RequiredArgsConstructor;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;
import org.learning.sprinbootapitrest.persons.dto.PersonName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PersonController {
    private final PersonRepository personRepository;

    @GetMapping("persons")
    public ResponseEntity<?> getPersons() {
        List<Person> personsList = personRepository.getAll();
        return personsList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(personsList);
    }

    @GetMapping("persons/name/{name}")
    public ResponseEntity<?> getPersonsByMatchingName(@PathVariable String name) {
        List<PersonDTO> personsDTOList = personRepository.findByName(name);
        return personsDTOList.size() != 0 ? ResponseEntity.ok(personsDTOList) : ResponseEntity.noContent().build();
    }

    @GetMapping("persons/{id}")
    public ResponseEntity<?> getPerson(@PathVariable Integer id) {
        PersonDTO personDTO = personRepository.findById(id);
        return personDTO != null ? ResponseEntity.ok(personDTO) : ResponseEntity.noContent().build();
    }

    @PostMapping("persons")
    public ResponseEntity<?> createPerson(@RequestBody PersonDTO person) {
        PersonDTO personDTO = personRepository.save(person);
        return personDTO != null ?
                ResponseEntity.status(HttpStatus.CREATED).body(person) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("persons/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable int id) {
        Person person = personRepository.deleteById(id);
        return person != null ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("persons/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable int id, @RequestBody PersonDTO person) {
        PersonDTO personDTO = personRepository.save(person, id);
        return personDTO != null ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PatchMapping("persons/{id}")
    public ResponseEntity<?> patchPerson(@PathVariable int id, @RequestBody PersonName personName) {
        PersonName personNameUpdated = personRepository.save(personName, id);
        return personNameUpdated != null ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
