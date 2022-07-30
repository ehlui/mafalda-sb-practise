package org.learning.sprinbootapitrest.persons;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.sprinbootapitrest.persons.dto.PersonDTO;
import org.learning.sprinbootapitrest.persons.dto.PersonName;
import org.learning.sprinbootapitrest.persons.errors.PersonNotFoundException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PersonRepository mockPersonRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        Mockito.when(mockPersonRepository.getAll()).thenReturn(loadPersons());
    }

    @Test
    void itShouldGetAllRecordsFromRepository() throws Exception {
        when(mockPersonRepository.getAll()).thenReturn(loadPersons());

        String expectedResponse = "[" +
                "{id:1,name:Laura,age:30}," +
                "{id:2,name:Mariano,age:21}," +
                "{id:3,name:Paopalo,age:19}," +
                "{id:4,name:Yamoto,age:60}]";

        RequestBuilder request = MockMvcRequestBuilders
                .get("/persons")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse))
                .andReturn();
    }

    @Test
    void itShouldGetNoContentWhenNoPersonIsInTheList() throws Exception {
        when(mockPersonRepository.getAll()).thenReturn(Collections.emptyList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/persons")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    void itShouldGetAListOfPersonsByMatchingName() throws Exception {
        when(mockPersonRepository.findByName("laura"))
                .thenReturn(List.of(new PersonDTO("Laura", 40)));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/persons/name/laura")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("[{name:Laura,age:40}]"))
                .andReturn();
    }

    @Test
    void itShouldNotGetAListOfPersonsByANonMatchingName() throws Exception {
        when(mockPersonRepository.findByName("marianos")).thenReturn(Collections.emptyList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/persons/name/marianos")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    void itShouldFindAPersonByItsId() throws Exception {
        when(mockPersonRepository.findById(1))
                .thenReturn(new PersonDTO("Laura", 30));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{name:Laura,age:30}"))
                .andReturn();
    }


    @Test
    void itShouldNotFindAPersonByNonMatchingId() throws Exception {
        when(mockPersonRepository.findById(100))
                .thenThrow(new PersonNotFoundException(100));

        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(100);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/persons/{id}", 100)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();
    }

    @Test
    void itShouldSaveAPersonToRepositoryList() throws Exception {
        PersonDTO person = new PersonDTO("Lucia", 222);

        when(mockPersonRepository.save(person))
                .thenReturn(person);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json("{name:Lucia,age:222}"))
                .andReturn();

        verify(mockPersonRepository).save(person);
    }

    @Test
    void itShouldNotSaveAPersonToRepositoryListWhenAgeIsNegative() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PersonDTO("Lucia", -1)));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", Is.is("BAD_REQUEST")))
                // TODO: Handle exceptions correctly - e.g. https://www.baeldung.com/spring-mvc-test-exceptions
                .andExpect(content().string(Matchers.containsString("Validation failed for argument [0]")))
                .andReturn();
    }

    @Test
    void itShouldDeletedAPersonFromRepositoryList() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

        verify(mockPersonRepository, times(1)).deleteById(1);
    }

    @Test
    void itShouldNotDeleteAPersonWithANonMatchingId() throws Exception {
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(10);

        Mockito.doThrow(new PersonNotFoundException(10))
                .when(mockPersonRepository)
                .deleteById(10);

        RequestBuilder request = MockMvcRequestBuilders.
                delete("/persons/{id}", 10);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();
    }

    @Test
    void itShouldUpdateAPersonData() throws Exception {
        PersonDTO updatePersonDto = new PersonDTO("Lauriko", 21);
        String body = objectMapper.writeValueAsString(updatePersonDto);

        when(mockPersonRepository.save(updatePersonDto, 5)).thenReturn(updatePersonDto);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/persons/{id}", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();

        verify(mockPersonRepository).save(updatePersonDto, 5);
    }

    @Test
    void itShouldNotUpdateAPersonDataWhenPersonDoesNotExistInThePersonsList() throws Exception {
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(5);
        PersonDTO updatePersonDto = new PersonDTO("Lauriko", 21);
        String body = objectMapper.writeValueAsString(updatePersonDto);

        when(mockPersonRepository.save(updatePersonDto, 5))
                .thenThrow(new PersonNotFoundException(5));

        RequestBuilder request = MockMvcRequestBuilders
                .put("/persons/{id}", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();

        verify(mockPersonRepository).save(updatePersonDto, 5);
    }


    @Test
    void itShouldUpdatePersonsName() throws Exception {
        PersonName newPersonsName = new PersonName("Mauricio");
        String body = objectMapper.writeValueAsString(newPersonsName);

        when(mockPersonRepository.save(newPersonsName, 5)).thenReturn(newPersonsName);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/persons/{id}", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();

        verify(mockPersonRepository).save(new PersonName("Mauricio"), 5);
    }

    @Test
    void itShouldNotUpdatePersonsNameWhenIdIsNoMatchingPerson() throws Exception {
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(5);
        PersonName newPersonsName = new PersonName("Mauricio");
        String body = objectMapper.writeValueAsString(newPersonsName);

        Mockito.when(mockPersonRepository.save(newPersonsName, 5))
                .thenThrow(new PersonNotFoundException(5));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/persons/{id}", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();
    }

    private List<Person> loadPersons() {
        return List.of(new Person(1, "Laura", 30),
                new Person(2, "Mariano", 21),
                new Person(3, "Paopalo", 19),
                new Person(4, "Yamoto", 60));
    }
}