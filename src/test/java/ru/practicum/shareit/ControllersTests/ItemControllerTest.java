package ru.practicum.shareit.ControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    @Autowired
    private final ObjectMapper objectMapper;
    @MockBean
    private final ItemService itemService;

    ItemDto itemDto = ItemDto.builder()
            .id(1L).name("Name").description("Desc").available(true).requestId(null)
            .build();
    ItemDto itemDtoUpdated = ItemDto.builder()
            .id(1L).name("NewName").description("Desc").available(false).requestId(null)
            .build();

    ItemDto itemDto2 = ItemDto.builder()
            .id(2L).name("Name2").description("Desc2").available(false).requestId(null)
            .build();

    ItemDto itemDto3 = ItemDto.builder()
            .id(3L).name("Name3").description("Desc3").available(true).requestId(1L)
            .build();
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldAddNewItem() throws Exception {
        when(itemService.add(any(), any())).thenReturn(itemDto);
        String json = objectMapper.writeValueAsString(itemDto);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(nullValue()));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        when(itemService.update(any(), any(), any())).thenReturn(itemDtoUpdated);
        String json = objectMapper.writeValueAsString(itemDtoUpdated);
        mockMvc.perform(patch("/items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.requestId").value(nullValue()));
    }

    @Test
    public void shouldSearch() throws Exception {
        when(itemService.search("Name", 0, 10)).thenReturn(List.of(itemDto));
        String json = objectMapper.writeValueAsString(List.of(itemDto));
        mockMvc.perform(get("/items/search?text=Name&from=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].description").value("Desc"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].requestId").value(nullValue()));
    }

    @Test
    public void shouldReturnItemDtoByIdWithoutBookingsAndComments() throws Exception {

        User user = User.builder().id(1L).name("Name").email("mail@mail.ru").build();
        when(itemService.get(any(), any()))
                .thenReturn(ItemMapper.toItemRDto(
                        ItemMapper.toItem(itemDto2, user, null), List.of(), List.of()));

        mockMvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Name2"))
                .andExpect(jsonPath("$.description").value("Desc2"))
                .andExpect(jsonPath("$.lastBooking").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.nextBooking").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.comments").isEmpty());
    }
/*
    @Test
    public void shouldReturnUsersList() throws Exception {

        when(userService.getAll()).thenReturn(List.of(userDto, userDto2, userDto3));

        String json2 = objectMapper.writeValueAsString(userDto2);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(json2)).andReturn();

        String json3 = objectMapper.writeValueAsString(userDto3);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON).content(json3)).andReturn();
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

 */
}
