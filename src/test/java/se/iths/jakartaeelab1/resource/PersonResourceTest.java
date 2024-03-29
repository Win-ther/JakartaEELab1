package se.iths.jakartaeelab1.resource;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.iths.jakartaeelab1.dto.PersonDto;
import se.iths.jakartaeelab1.dto.Persons;
import se.iths.jakartaeelab1.entity.Person;
import se.iths.jakartaeelab1.service.PersonService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonResourceTest {
    private static final UUID id = UUID.randomUUID();
    @Mock
    PersonService personService;
    Dispatcher dispatcher;

    @BeforeEach
    void setup() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        var resource = new PersonResource(personService);
        dispatcher.getRegistry().addSingletonResource(resource);
    }

    @Test
    @DisplayName("get all persons as a list with GET should return status 200")
    void getAllPersonsAsAListShouldReturnStatus200() throws Exception {
        when(personService.allPersons()).thenReturn(new Persons(List.of()));

        MockHttpRequest request = MockHttpRequest.get("/persons");
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(200, response.getStatus());
        assertEquals("{\"persons\":[]}", response.getContentAsString());
    }

    @Test
    @DisplayName("finding one person by id with GET should return status 200")
    void findingOnePersonByIdShouldReturnStatus200() throws Exception {
        PersonDto personDto = new PersonDto("Peter", 49, "Doctor");

        when(personService.onePerson(id)).thenReturn(personDto);

        MockHttpRequest request = MockHttpRequest.get("/persons/" + id);
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(200, response.getStatus());
        assertEquals("{\"name\":\"Peter\",\"age\":49,\"profession\":\"Doctor\"}", response.getContentAsString());
    }

    @Test
    @DisplayName("not finding a person by id with GET should return status 404")
    void notFindingAPersonByIdShouldReturnStatus404() throws Exception {
        when(personService.onePerson(id)).thenThrow(new NotFoundException());

        MockHttpRequest request = MockHttpRequest.get("/persons/" + id);
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("create new person with POST should return status 201")
    void createNewPersonShouldReturnStatus201() throws Exception {
        when(personService.addPerson(any())).thenReturn(new Person());

        MockHttpRequest request = MockHttpRequest.post("/persons");
        request.contentType(MediaType.APPLICATION_JSON);
        request.content("{\"name\":\"Eleonore\",\"age\":33,\"profession\":\"Personal trainer\"}".getBytes());
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(201, response.getStatus());
    }

    @Test
    @DisplayName("update person with PATCH should return status 200")
    void updatePersonShouldReturnStatus200() throws Exception{
        PersonDto existingPersonDto = new PersonDto("Anna", 30, "Marketing");

        when(personService.updatePerson(any(UUID.class), any(PersonDto.class)))
                .thenReturn(existingPersonDto);

        MockHttpRequest request = MockHttpRequest.patch("/persons/" + id);
        request.contentType(MediaType.APPLICATION_JSON);
        request.content("{\"name\":\"Louise\",\"age\":25,\"profession\":\"Soldier\"}".getBytes());
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("non existing id when updating person with PATCH should return status 404")
    void nonExistingIdWhenUpdatingPersonShouldReturnStatus404() throws Exception{
        when(personService.updatePerson(any(UUID.class), any(PersonDto.class)))
                .thenThrow(new NotFoundException());

        MockHttpRequest request = MockHttpRequest.patch("/persons/" + id);
        request.contentType(MediaType.APPLICATION_JSON);
        request.content("{\"name\":\"Steve\",\"age\":55,\"profession\":\"Sailor\"}".getBytes());
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("delete person with DELETE should return status 204")
    void deletePersonShouldReturnStatus204() throws Exception{
        MockHttpRequest request = MockHttpRequest.delete("/persons/" + id);
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(204, response.getStatus());
    }

    @Test
    @DisplayName("non existing id when deleting person with DELETE should return status 404")
    void nonExistingIdWhenDeletingPersonShouldReturnStatus404() throws Exception {
        doThrow(new NotFoundException()).when(personService).removePerson(id);

        MockHttpRequest request = MockHttpRequest.delete("/persons/" + id);
        MockHttpResponse response = new MockHttpResponse();
        dispatcher.invoke(request, response);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("PersonResource empty constructor check")
    void personResourceEmptyConstructorCheck() {
        PersonResource personResource = new PersonResource();
        assertEquals(PersonResource.class, personResource.getClass());
    }

}