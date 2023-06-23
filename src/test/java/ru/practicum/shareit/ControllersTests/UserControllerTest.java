package ru.practicum.shareit.ControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    @Autowired
    private final ObjectMapper objectMapper;
    @MockBean
    private final UserService userService;
    UserDto userDto = UserDto.builder().id(1L).name("Name").email("mail@mail.ru").build();
    UserDto userDto2 = UserDto.builder().id(2L).name("Name2").email("mail2@mail.ru").build();
    UserDto userDto3 = UserDto.builder().id(3L).name("Name3").email("mail3@mail.ru").build();
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldAddNewUser() throws Exception {
        when(userService.add(any())).thenReturn(userDto);
        String json = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"));
    }

    @Test
    public void shouldReturnUsersList() throws Exception {

        when(userService.getAll()).thenReturn(List.of(userDto, userDto2, userDto3));

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].email").value("mail@mail.ru"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].email").value("mail2@mail.ru"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Name3"))
                .andExpect(jsonPath("$[2].email").value("mail3@mail.ru"));
    }

    @Test
    public void shouldReturnUserDtoById() throws Exception {
        when(userService.get(3L)).thenReturn(userDto3);

        mockMvc.perform(get("/users/{id}", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Name3"))
                .andExpect(jsonPath("$.email").value("mail3@mail.ru"));
    }

    @Test
    public void shouldUpdateUserDto() throws Exception {
        UserDto userDto4 = userDto3;
        userDto4.setEmail("kek@kek.ru");
        String json4 = objectMapper.writeValueAsString(userDto4);
        when(userService.update(any(), any())).thenReturn(userDto4);

        mockMvc.perform(patch("/users/{id}", 3)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json4)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Name3"))
                .andExpect(jsonPath("$.email").value("kek@kek.ru"));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
    }
}
