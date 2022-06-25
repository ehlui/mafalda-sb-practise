package org.learning.sprinbootapitrest.persons;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PersonRepository mockPersonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    PersonControllerTest() {
    }

    @BeforeEach
    public void setUp() {
        Mockito.when(mockPersonRepository.getAll()).thenReturn(loadPersons());
    }

    @Test
    void itShouldGetAllRecordsFromRepository() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(mockPersonRepository.getAll());
        assertThat(response).contains(expectedResponse);
    }

    @Test
    void itShouldGetNoContentWhenNoPersonIsInTheList() throws Exception {
        Mockito.when(mockPersonRepository.getAll()).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mvc.perform(get("/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isEmpty();
    }

    @Test
    void itShouldGetAListOfPersonsByMatchingName() throws Exception {
        List<PersonDTO> personDTOList = List.of(new PersonDTO("Laura", 40));
        Mockito.when(mockPersonRepository.findByName("laura")).thenReturn(personDTOList);

        MvcResult mvcResult = mvc.perform(get("/persons/name/laura")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(personDTOList);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void itShouldNotGetAListOfPersonsByANonMatchingName() throws Exception {
        Mockito.when(mockPersonRepository.findByName("marianos")).thenReturn(Collections.emptyList());

        MvcResult mvcResult = mvc.perform(get("/persons/name/marianos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEmpty();
    }

    @Test
    void itShouldFindAPersonByItsId() throws Exception {
        PersonDTO person = new PersonDTO("Laura", 30);
        final String expectedResponseContent = objectMapper.writeValueAsString(person);

        Mockito.when(mockPersonRepository.findById(1)).thenReturn(person);

        MvcResult mvcResult = mvc.perform(get("/persons/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(mockPersonRepository.findById(1));
        assertThat(response).isEqualTo(expectedResponse);
    }


    @Test
    void itShouldNotFindAPersonByNonMatchingId() throws Exception {
        final int nonMatchingId = 100;
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(nonMatchingId);

        Mockito.when(mockPersonRepository.findById(nonMatchingId))
                .thenThrow(new PersonNotFoundException(nonMatchingId));

        MvcResult mvcResult = mvc.perform(get("/persons/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
    }

    @Test
    void itShouldSaveAPersonToRepositoryList() throws Exception {
        Person person = new Person(5, "Lucia", 222);
        PersonDTO newPersonDto = new PersonDTO(person.getName(), person.getAge());
        List<Person> personList = loadPersons();
        String body = objectMapper.writeValueAsString(newPersonDto);

        Mockito.when(mockPersonRepository.save(newPersonDto)).thenReturn(newPersonDto);
        Mockito.when(mockPersonRepository.findById(5)).thenReturn(newPersonDto);
        personList.add(person);        // After adding one person
        Mockito.when(mockPersonRepository.getAll()).thenReturn(personList);

        MvcResult mvcResult = mvc
                .perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(body))
                .andReturn();

        MvcResult mvcResultFindById = mvc
                .perform(get("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        MvcResult mvcResultAll = mvc
                .perform(get("/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String responseFindById = mvcResultFindById.getResponse().getContentAsString();
        String personSaved = objectMapper.writeValueAsString(person);
        String allPersonsAfterSaveOne = mvcResultAll.getResponse().getContentAsString();

        assertThat(response).isEqualTo(body);
        assertThat(response).contains(responseFindById);
        assertThat(allPersonsAfterSaveOne).contains(personSaved);
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
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Is.is("BAD_REQUEST")))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
    }

    @Test
    void itShouldDeletedAPersonFromRepositoryList() throws Exception {
        MvcResult mvcResult = mvc
                .perform(delete("/persons/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andReturn();

        Mockito.verify(mockPersonRepository, times(1))
                .deleteById(1);

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isEmpty();
    }

    @Test
    void itShouldNotDeleteAPersonWithANonMatchingId() throws Exception {
        final int nonMatchingId = 10;
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(nonMatchingId);

        Mockito.doThrow(new PersonNotFoundException(nonMatchingId))
                .when(mockPersonRepository)
                .deleteById(nonMatchingId);

        MvcResult mvcResult = mvc
                .perform(delete("/persons/{id}", nonMatchingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
    }

    @Test
    void itShouldUpdateAPersonData() throws Exception {
        PersonDTO updatePersonDto = new PersonDTO("Lauriko", 21);
        String body = objectMapper.writeValueAsString(updatePersonDto);

        Mockito.when(mockPersonRepository.save(updatePersonDto, 5)).thenReturn(updatePersonDto);
        Mockito.when(mockPersonRepository.findById(5)).thenReturn(updatePersonDto);

        MvcResult mvcResult = mvc
                .perform(put("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult mvcResultFindByIdAfterUpdate = mvc
                .perform(get("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String personUpdated = objectMapper.writeValueAsString(updatePersonDto);
        String responseFindByIdAfterUpdate = mvcResultFindByIdAfterUpdate.getResponse().getContentAsString();

        assertThat(response).isEmpty();
        assertThat(responseFindByIdAfterUpdate).isEqualTo(personUpdated);
    }

    @Test
    void itShouldNotUpdateAPersonDataWhenPersonDoesNotExistInThePersonsList() throws Exception {
        final int nonMatchingId = 5;
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(nonMatchingId);
        PersonDTO updatePersonDto = new PersonDTO("Lauriko", 21);
        String body = objectMapper.writeValueAsString(updatePersonDto);

        Mockito.when(mockPersonRepository.save(updatePersonDto, nonMatchingId))
                .thenThrow(new PersonNotFoundException(nonMatchingId));

        MvcResult mvcResult = mvc
                .perform(put("/persons/{id}", nonMatchingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();
    }


    @Test
    void itShouldUpdatePersonsName() throws Exception {
        PersonName newPersonsName = new PersonName("Mauricio");
        PersonDTO personDTO = new PersonDTO(newPersonsName.getName(), 10);
        String body = objectMapper.writeValueAsString(newPersonsName);

        Mockito.when(mockPersonRepository.save(newPersonsName, 5)).thenReturn(newPersonsName);
        Mockito.when(mockPersonRepository.findById(5)).thenReturn(personDTO);

        MvcResult mvcResult = mvc
                .perform(patch("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        MvcResult mvcResultAfterUpdateName = mvc
                .perform(get("/persons/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String personUpdated = objectMapper.writeValueAsString(personDTO);
        String response = mvcResult.getResponse().getContentAsString();
        String personAfterUpdate = mvcResultAfterUpdateName.getResponse().getContentAsString();

        assertThat(response).isEmpty();
        assertThat(personUpdated).isEqualTo(personAfterUpdate);
    }

    @Test
    void itShouldNotUpdatePersonsNameWhenIdIsNoMatchingPerson() throws Exception {
        final int nonMatchingId = 5;
        String exceptionMessage = "Person with id '%d' cannot be found! It may not exists.".formatted(nonMatchingId);
        PersonName newPersonsName = new PersonName("Mauricio");
        String body = objectMapper.writeValueAsString(newPersonsName);

        Mockito.when(mockPersonRepository.save(newPersonsName, nonMatchingId))
                .thenThrow(new PersonNotFoundException(nonMatchingId));

        MvcResult mvcResult = mvc
                .perform(patch("/persons/{id}", nonMatchingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PersonNotFoundException))
                .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))

                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
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