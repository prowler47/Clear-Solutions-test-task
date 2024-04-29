package ua.dragunovskiy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.dragunovskiy.entity.User;
import ua.dragunovskiy.service.UsersService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
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
        UUID id = UUID.randomUUID();
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
    public void partialUpdateInvalidRequestTest() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(MockMvcRequestBuilders.patch("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void noSuchUserFoundPartialUpdateTest() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();

        Mockito.doThrow(new NullPointerException("User not found")).when(service).partialUpdate(id, user);
        mvc.perform(MockMvcRequestBuilders.patch("/users/" +id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void allUpdateSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLastName("user");
        user.setLastName("user");
        user.setAddress("address");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setPhone("1234567");
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
        Mockito.doNothing().when(service).allUpdate(id, user);

        mvc.perform(MockMvcRequestBuilders.patch("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fieldsForUpdate))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void notFieldsAllUpdateTest() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("user");

        String content = """
                {
                    "email": "test-eemail TEST2",
                    "firstName": "test - updated 2",
                    "lastName": "test1111",
                    "birthday": "2000-01-12",
                    "address": "123street"
                }
                """;

        Mockito.doReturn(status().isBadRequest());
        mvc.perform(MockMvcRequestBuilders.put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void noSuchUserDeleteTest() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doThrow(new RuntimeException()).when(service).delete(id);

        mvc.perform(MockMvcRequestBuilders.delete("/users/" + id))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void successDeleteTest() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(service).delete(id);
        mvc.perform(MockMvcRequestBuilders.delete("/users/" + id))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUsersByBirthDayTest() throws Exception {
        String from = "2000-01-01";
        String to = "1999-06-12";
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(new User(UUID.randomUUID(), "user1@email.com", "user", "user", LocalDate.of(1998, 1, 1), "address", "123456"));
        mockUsers.add(new User(UUID.randomUUID(), "user2@email.com", "user2", "user2", LocalDate.of(2000, 4, 5), "address", "123456"));
        mockUsers.add(new User(UUID.randomUUID(), "user3@email.com", "user3", "user3", LocalDate.of(2002, 5, 6), "address", "123456"));

        Mockito.when(service.getUsersByBirthday(LocalDate.parse(from), LocalDate.parse(to)))
                .thenReturn(mockUsers);

        mvc.perform(MockMvcRequestBuilders.get("/users")
                .param("from", from)
                .param("to", to)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUsersByBirthdayNoSuchFoundTest() throws Exception {
        String from = "2000-01-01";
        String to = "1999-06-12";
        List<User> emptyList = new ArrayList<>();
        Mockito.when(service.getUsersByBirthday(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(emptyList);

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("from", from)
                        .param("to", to)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
