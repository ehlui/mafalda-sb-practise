package org.learning.sprinbootapitrest.persons;

import lombok.NonNull;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;
import org.learning.sprinbootapitrest.persons.dto.PersonName;
import org.learning.sprinbootapitrest.persons.errors.PersonNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Repository along with an in-memory list as if it were the actual DB to use our Controller with some data persistence
 */
@Repository
public class PersonRepository {
    private static final Logger log = LoggerFactory.getLogger(PersonRepository.class);
    private List<Person> personList;

    public PersonRepository() {
        init();
    }

    public PersonDTO save(@NonNull PersonDTO personDTO) {
        Person person = new Person(generateConsecutiveId(), personDTO.getName(), personDTO.getAge());
        this.personList.add(person);
        return personDTO;
    }

    public PersonDTO save(@NonNull PersonDTO person, int id) throws PersonNotFoundException {
        return findOptionalById(id)
                .map(p ->
                {
                    p.setAge(person.getAge());
                    p.setName(person.getName());
                    return new PersonDTO(person.getName(), person.getAge());
                })
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    public PersonName save(@NonNull PersonName personName, int id) throws PersonNotFoundException {
        return personList.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(p ->
                {
                    p.setName(personName.getName());
                    return personName;
                })
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    public Optional<Person> findOptionalById(int id) {
        return personList.stream().filter(p -> p.getId() == id).findFirst();
    }

    public PersonDTO findById(int id) throws PersonNotFoundException {
        return findOptionalById(id)
                .map(p -> new PersonDTO(p.getName(), p.getAge()))
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    public List<PersonDTO> findByName(@NonNull String name) {
        return personList
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .map(p -> new PersonDTO(p.getName(), p.getAge()))
                .toList();
    }

    public void deleteById(int id) throws PersonNotFoundException {
        Person person = findOptionalById(id).orElseThrow(() -> new PersonNotFoundException(id));
        this.personList.remove(person);
    }

    public List<Person> getAll() {
        return personList;
    }

    /**
     * As we are using in-memory list, we need to handle the consecutive IDs
     * <p>
     * If the list is not empty it will find the max ID value and add 1.
     * If the list is empty it will create an object, not referenced (to be deleted by gc)
     * and return the id + 1.
     *
     * @return the consecutive ID integer number
     */
    private int generateConsecutiveId() {
        return this.personList
                .stream()
                .max(Comparator.comparingInt(Person::getId))
                .orElse(new Person(0, "", 0))
                .getId() + 1;
    }

    /**
     * Starting our in-memory list as if it were our H2 or preferred DB.
     */
    public void init() {
        this.personList = Collections.synchronizedList(new ArrayList<>());
        personList.add(new Person(1, "Laura", 30));
        personList.add(new Person(2, "Mariano", 21));
        personList.add(new Person(3, "Paopalo", 19));
        personList.add(new Person(4, "Yamoto", 60));

        log.info("List of person has been created: " + this.personList);
    }
}
