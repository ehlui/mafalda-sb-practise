package org.learning.sprinbootapitrest.persons;

import lombok.RequiredArgsConstructor;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;
import org.learning.sprinbootapitrest.persons.dto.PersonName;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PersonController {
    private final PersonRepository personRepository;

    @GetMapping("persons")
    public List<Person> getPersons() {
        return personRepository.getAll();
    }

    @GetMapping("persons/name/{name}")
    public List<PersonDTO> getPersonsByMatchingName(@PathVariable String name) {
        return personRepository.findByName(name);
    }

    @GetMapping("persons/{id}")
    public PersonDTO getPerson(@PathVariable Integer id) {
        return personRepository.findById(id);
    }

    @PostMapping("persons")
    public PersonDTO createPerson(@RequestBody PersonDTO person) {
        return personRepository.save(person);
    }

    @DeleteMapping("persons/{id}")
    public Person deletePerson(@PathVariable int id) {
        return this.personRepository.deleteById(id);
    }

    @PutMapping("persons/{id}")
    public PersonDTO updatePerson(@PathVariable int id, @RequestBody PersonDTO person) {
        return this.personRepository.save(person, id); // TODO: Good practices REST -> PUT does not return anything (204 or 404 for resource not found)
    }

    @PatchMapping("persons/{id}")
    public PersonName patchPerson(@PathVariable int id, @RequestBody PersonName personName) {
        return this.personRepository.save(personName, id);
    }
}
