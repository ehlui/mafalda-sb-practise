package org.learning.sprinbootapitrest.persons;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;
import org.learning.sprinbootapitrest.persons.dto.PersonName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
//@WebMvcTest(PersonController.class) -> This'll avoid to load all context (only web layer)
@AutoConfigureMockMvc // without the cost of starting the server
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void itShouldHaveAListOfFourPersonsFromRepositoryAtStarting() {
        assertThat(personRepository).isNotNull();
        assertThat(personRepository.getAll()).hasSize(4);
        assertThat(personRepository.getAll()).isEqualTo(loadPersons());
    }

    @Test
    void itShouldGetAllRecordsFromRepository() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(personRepository.getAll());
        assertThat(response).contains(expectedResponse);
    }

    @Test
    void itShouldGetAListOfPersonsByMatchingName() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/persons/name/laura")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(personRepository.findByName("laura"));
        assertThat(response).contains(expectedResponse);
    }

    @Test
    void itShouldNotGetAListOfPersonsByANonMatchingName() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/persons/name/marianos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(personRepository.findByName("marianos"));

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response).isEqualTo("[]");

    }

    @Test
    void itShouldFindAPersonByItsId() throws Exception {
        PersonDTO person = new PersonDTO("Laura", 30);
        final String expectedResponseContent = objectMapper.writeValueAsString(person);

        MvcResult mvcResult = mvc.perform(get("/persons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(personRepository.findById(1));
        assertThat(response).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @Test
    void itShouldSaveAPersonToRepositoryList() throws Exception {
        PersonDTO newPersonDto = new PersonDTO("Lucia", 222);
        Person newPerson = new Person(5, newPersonDto.getName(), newPersonDto.getAge());
        String body = objectMapper.writeValueAsString(newPersonDto);

        MvcResult mvcResult = mvc
                .perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(body))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<Person> personsList = personRepository.getAll();
        assertThat(response).isEqualTo(body);
        assertThat(personsList).contains(newPerson);
        assertThat(personsList).hasSize(5);
    }

    @Test
    void itShouldNotSaveAPersonToRepositoryListWhenAgeIsNegative() throws Exception {
        PersonDTO newPersonDto = new PersonDTO("Lucia", -1);
        String body = objectMapper.writeValueAsString(newPersonDto);

        MvcResult mvcResult = mvc
                .perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();


        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isEmpty();
        assertThat(personRepository.getAll()).hasSize(4);

    }

    @Test
    void itShouldDeletedAPersonFromRepositoryListAndEnsureListSize() throws Exception {
        PersonDTO newPersonDto = new PersonDTO("Sasiolo", 40);
        Person newPersonToBeDelete = new Person(5, newPersonDto.getName(), newPersonDto.getAge());
        String body = objectMapper.writeValueAsString(newPersonDto);

        // Creating a dummy person to be saved and ensure it was saved correctly
        personRepository.save(newPersonDto);
        assertThat(personRepository.getAll()).hasSize(5);

        MvcResult mvcResult = mvc
                .perform(delete("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(body))
                .andReturn();

        String expectedResult = objectMapper.writeValueAsString(newPersonToBeDelete);
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEqualTo(expectedResult);
        assertThat(personRepository.getAll()).hasSize(4);
    }

    @Test
    void itShouldNotDeleteAPersonWithANonMatchingId() throws Exception {
        MvcResult mvcResult = mvc
                .perform(delete("/persons/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEmpty();
        assertThat(personRepository.getAll()).hasSize(4);
    }

    @Test
    void itShouldUpdateAPersonData() throws Exception {
        PersonDTO newPersonDto = new PersonDTO("Lauriano", 20);
        PersonDTO updatePersonDto = new PersonDTO("Lauriko", 21);

        String body = objectMapper.writeValueAsString(updatePersonDto);

        personRepository.save(newPersonDto);
        assertThat(personRepository.getAll()).hasSize(5);

        MvcResult mvcResult = mvc
                .perform(put("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String personFromListUpdated = objectMapper.writeValueAsString(personRepository.findById(5));
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEqualTo(body);
        assertThat(response).isEqualTo(personFromListUpdated);
        assertThat(personRepository.deleteById(5))
                .isEqualTo(new Person(5, updatePersonDto.getName(), updatePersonDto.getAge()));
    }

    @Test
    void itShouldNotUpdateAPersonDataWhenPersonDoesNotExistInThePersonsList() throws Exception {
        PersonDTO updatePersonDto = new PersonDTO("Lauriko", 21);
        String body = objectMapper.writeValueAsString(updatePersonDto);

        MvcResult mvcResult = mvc
                .perform(put("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEmpty();
        assertThat(personRepository.findById(5)).isNull();
    }


    @Test
    void itShouldUpdatePersonsNameFeature() throws Exception {
        PersonName newPersonsName = new PersonName("Mauricio");
        PersonDTO dummyPerson = new PersonDTO("Mauri", 21);
        String body = objectMapper.writeValueAsString(newPersonsName);

        personRepository.save(dummyPerson);
        assertThat(personRepository.getAll()).hasSize(5);

        MvcResult mvcResult = mvc
                .perform(patch("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String personWithNameUpdatedFromList = objectMapper.writeValueAsString(personRepository.findById(5));
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEqualTo("{\"name\":\"Mauricio\"}");
        assertThat(personWithNameUpdatedFromList).isEqualTo("{\"name\":\"Mauricio\",\"age\":21}");
        assertThat(personRepository.deleteById(5))
                .isEqualTo(new Person(5, newPersonsName.getName(), dummyPerson.getAge()));
    }

    private List<Person> loadPersons() {
        List<Person> personList = Collections.synchronizedList(new ArrayList<>());
        personList.add(new Person(1, "Laura", 30));
        personList.add(new Person(2, "Mariano", 21));
        personList.add(new Person(3, "Paopalo", 19));
        personList.add(new Person(4, "Yamoto", 60));

        return personList;
    }
}