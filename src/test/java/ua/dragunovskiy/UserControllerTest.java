package ua.dragunovskiy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.dragunovskiy.entity.User;
import ua.dragunovskiy.service.AbstractService;
import ua.dragunovskiy.service.UsersService;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
//@SpringBootTest
////@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Mockito mockito;



    @MockBean
    private UsersService service;

    public UserControllerTest() {
    }

    @Test
    public void createUserWithValidAgeTest() throws Exception {
        String validAgeUserContent = """
                {
                      "email": "test-email-123",
                      "firstName": "user3",
                      "lastName": "test3",
                      "birthday": "2000-01-01",
                      "address": "123street",
                      "phone": "2323232"
                }
                """;
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validAgeUserContent))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void createUserWithInvalidAgeTest() throws Exception {
        String invalidAgeUserContent = """
                {
                    "email": "test-email1",
                    "firstName": "user1",
                    "birthday": "2024-01-01
                }
                """;
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAgeUserContent))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void partialUpdateUserTest() throws Exception {
        UUID id = UUID.fromString("1af6ef33-f58c-4f89-be19-66897f72ccd2");
        String fieldForUpdate = """
                {
                    "email": "test-email new update"
                }
                """;

        mvc.perform(MockMvcRequestBuilders.patch("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fieldForUpdate))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void noSuchUserFoundPartialUpdateTest() throws Exception {
        UUID noExistingId = UUID.fromString("43bb38d3-c2ad-4573-81b3-9466");
        String fieldForUpdate = """
                {
                    "email": "test-email new update"
                }
                """;
        mvc.perform(MockMvcRequestBuilders.patch("/users/" + noExistingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fieldForUpdate))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void allUpdateSuccessTest() throws Exception {
        UUID id = UUID.fromString("1af6ef33-f58c-4f89-be19-66897f72ccd2");
        String fieldsForUpdate = """
                {
                    "email": "test-email TEST2",
                    "firstName": "test - updated 2",
                    "lastName": "test1111",
                    "birthday": "2000-01-12",
                    "address": "123street",
                    "phone": "2323232"
                }
                """;
        mvc.perform(MockMvcRequestBuilders.patch("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fieldsForUpdate))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void notAllFieldsAllUpdateTest() throws Exception {
        UUID id = UUID.fromString("43bb38d3-c2ad-4573-81b3-9466aa6e4bfe");
        String fieldsForUpdate = """
                {
                    "email": "test-email TEST2",
                    "firstName": "test - updated 2",
                    "lastName": "test1111",
                    "birthday": "2000-01-12",
                    "address": "123street"
                }
                """;
        mvc.perform(MockMvcRequestBuilders.put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fieldsForUpdate))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void noSuchUserDeleteTest() throws Exception {
        UUID id = UUID.fromString("43bb38d3-c2ad-4573-81b3-9466");
        mvc.perform(MockMvcRequestBuilders.delete("/users/" + id))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void successDeleteTest() throws Exception {
        UUID id = UUID.fromString("b99dc2f7-89ae-43e9-ae5a-677d7e551924");

//        Mockito.doNothing().when(service).delete(id);
        mvc.perform(MockMvcRequestBuilders.delete("/users/" + id))
                .andExpect(status().isOk())
                .andReturn();
    }
}
