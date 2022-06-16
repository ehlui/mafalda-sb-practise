package org.learning.sprinbootapitrest.persons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersonRepositoryTest {

    private PersonRepository personRepository;

    @BeforeEach
    public void setup() {
        personRepository = new PersonRepository();
    }

    @Test
    @DisplayName("It should save a person into the in-memory list built-in")
    void itShouldSaveAPerson() {
        //given:
        PersonDTO personDTO = new PersonDTO("Felipe", 70);
        //when:
        assertNotNull(this.personRepository);
        personRepository.save(personDTO);
        //then:
        Person newPersonInList = new Person(5, personDTO.getName(), personDTO.getAge());
        // 1 to 4 ids starts the PersonRepository (list within) and adds 1 consecutively
        // That's why the number 5 will be the first added element (to be found)
        assertEquals(personRepository.findById(5), personDTO);
        assertThat(newPersonInList).isIn(personRepository.getAll());
    }

    @Test
    @DisplayName("It should not save a person with negative age")
    void itShouldNotSaveAPersonWithNegativeAge() {
        //given:
        PersonDTO personDTO = new PersonDTO("Felipe", -10);
        //when:
        assertNotNull(this.personRepository);
        PersonDTO personNotSaved = personRepository.save(personDTO);
        // then:
        assertNull(personNotSaved);
    }


    @Test
    @DisplayName("It should update a person along with an existing ID")
    void itShouldUpdateAPerson() {
        //given:
        final int personId = 1;
        PersonDTO newDataPersonDTO = new PersonDTO("MY-NEW-NAME", 123);

        //when:
        assertNotNull(this.personRepository);
        PersonDTO oldPersonData = personRepository.findById(1);
        assertNotNull(oldPersonData);

        personRepository.save(newDataPersonDTO, personId);
        //then:
        assertNotEquals(oldPersonData, newDataPersonDTO);
        assertEquals(personRepository.findById(personId), newDataPersonDTO);
    }

    @Test
    @DisplayName("It should not update a person with no matching ID")
    void itShouldNotUpdateAPersonWithNoMatchingId() {
        //given:
        final int notExistingPersonId = 99;
        PersonDTO newDataPersonDTO = new PersonDTO("MY-NEW-NAME", 123);
        //when:
        assertNotNull(this.personRepository);
        PersonDTO personSaved = personRepository.save(newDataPersonDTO, notExistingPersonId);
        //then:
        assertNull(personSaved);
    }


    @Test
    @DisplayName("It should find an optional of person by an ID")
    void itShouldFindAnOptionalOfPersonById() {
        //given:
        final int existingPersonId = 1;
        Person personToBeFound = new Person(1, "Laura", 30);
        //when:
        assertNotNull(this.personRepository);
        Optional<Person> personFound = personRepository.findOptionalById(existingPersonId);
        //then:
        assertThat(personFound).isNotEmpty();
        assertEquals(personFound.get(), personToBeFound);
    }

    @Test
    @DisplayName("It should not find an optional of person by not matching ID")
    void itShouldNotFindAnOptionalOfPersonByNotMatchingId() {
        //given:
        final  int existingPersonId = 123;
        //when:
        assertNotNull(this.personRepository);
        Optional<Person> personFound = personRepository.findOptionalById(existingPersonId);
        //then:
        assertThat(personFound).isEmpty();
    }

    @Test
    @DisplayName("It should find a person by ID")
    void itShouldFindAPersonById() {
        final  int existingPersonId = 1;
        //when:
        assertNotNull(this.personRepository);
        PersonDTO personFound = personRepository.findById(existingPersonId);
        //then:
        assertThat(personFound).isNotNull();
    }

    @Test
    @DisplayName("It should not find a person by not matching ID")
    void itShouldNotFindAPersonByNotMatchingId() {
        final int existingPersonId = 123;
        //when:
        assertNotNull(this.personRepository);
        PersonDTO personFound = personRepository.findById(existingPersonId);
        //then:
        assertThat(personFound).isNull();
    }

    @Test
    @DisplayName("It should find persons with a matching name")
    void itShouldFindPersonsWithAMatchingName() {
        final String matchingName = "Laura";
        //when:
        assertNotNull(this.personRepository);
        List<PersonDTO> matchingPersonsFound = personRepository.findByName(matchingName);
        //then:
        assertThat(matchingPersonsFound).isNotEmpty();
        assertThat(matchingPersonsFound).hasAtLeastOneElementOfType(PersonDTO.class);
    }

    @Test
    @DisplayName("It should not find persons with a non matching name")
    void itShouldNotFindPersonsWithANonMatchingName() {
        final String matchingName = "Mario";
        //when:
        assertNotNull(this.personRepository);
        List<PersonDTO> matchingPersonsFound = personRepository.findByName(matchingName);
        //then:
        assertThat(matchingPersonsFound).isEmpty();
    }

    @Test
    void itShouldDeleteAPersonByItsId() {
        final int existingPersonId = 1;
        //when:
        assertNotNull(this.personRepository);
        Person personDeleted = personRepository.deleteById(existingPersonId);
        //then:
        assertThat(personDeleted).isNotNull();
        assertThat(personDeleted).isNotIn(this.personRepository.getAll());
    }

    @Test
    void itShouldNotDeleteAPersonByNonMatchingId() {
        final int existingPersonId = 11;
        final int initPersonListSize = 4;
        //when:
        assertNotNull(this.personRepository);
        assertThat(this.personRepository.getAll()).hasSize(initPersonListSize);
        Person personDeleted = personRepository.deleteById(existingPersonId);
        //then:
        assertThat(personDeleted).isNull();
        assertThat(this.personRepository.getAll()).hasSize(initPersonListSize);
    }

    @Test
    void itShouldGetAllThePersonsAsAList() {
        final int TOTAL_INIT_SIZE = 4;
        //when:
        assertNotNull(this.personRepository);
        //then:
        assertThat(this.personRepository.getAll()).isNotEmpty();
        assertThat(this.personRepository.getAll()).hasAtLeastOneElementOfType(Person.class);
        assertThat(this.personRepository.getAll()).hasSize(TOTAL_INIT_SIZE);
    }
}