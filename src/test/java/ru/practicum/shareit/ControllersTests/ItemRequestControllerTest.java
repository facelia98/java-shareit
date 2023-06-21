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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    @Autowired
    private final ObjectMapper objectMapper;
    @MockBean
    private final ItemRequestService itemRequestService;
    User user = User.builder().id(1L).name("Name").email("mail@mail.ru").build();
    ItemDto item = ItemDto.builder().id(1L).name("Item").description("Desc").available(true).build();
    ItemDto item2 = ItemDto.builder().id(2L).name("Item2").description("Desc2").available(false).build();
    ItemRequest itemRequest =
            ItemRequest.builder().id(1L).description("Ручка").requestor(user).created(LocalDateTime.now()).build();
    ItemRequest itemRequest2 =
            ItemRequest.builder().id(2L).description("Карандаш").requestor(user).created(LocalDateTime.now()).build();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnItemRequestListForOthers() throws Exception {
        ItemRequestDtoReturned returned = ItemRequestMapper.toItemRequestDtoReturned(itemRequest, List.of());
        ItemRequestDtoReturned returned2 = ItemRequestMapper.toItemRequestDtoReturned(itemRequest2, List.of());
        when(itemRequestService.getAllFromOthers(any(), anyInt(), anyInt())).thenReturn(List.of(returned, returned2));

        String json = objectMapper.writeValueAsString(List.of(returned, returned2));

        mockMvc.perform(get("/requests/all?from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    public void shouldReturnItemRequestList() throws Exception {
        ItemRequestDtoReturned returned = ItemRequestMapper.toItemRequestDtoReturned(itemRequest, List.of());
        ItemRequestDtoReturned returned2 = ItemRequestMapper.toItemRequestDtoReturned(itemRequest2, List.of());
        when(itemRequestService.getAll(any())).thenReturn(List.of(returned, returned2));

        String json = objectMapper.writeValueAsString(List.of(returned, returned2));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    public void shouldReturnItemRequestWithoutList() throws Exception {
        ItemRequestDtoReturned returned = ItemRequestMapper.toItemRequestDtoReturned(itemRequest, List.of());
        when(itemRequestService.get(any(), any())).thenReturn(returned);

        String json = objectMapper.writeValueAsString(returned);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Ручка"))
                .andExpect(jsonPath("$.requestor.id").value(1))
                .andExpect(jsonPath("$.requestor.name").value("Name"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    public void shouldAddNewItemRequestWithoutList() throws Exception {
        ItemRequestDtoReturned returned = ItemRequestMapper.toItemRequestDtoReturned(itemRequest, List.of());
        when(itemRequestService.add(any(), any())).thenReturn(returned);

        String json = objectMapper.writeValueAsString(returned);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Ручка"))
                .andExpect(jsonPath("$.requestor.id").value(1))
                .andExpect(jsonPath("$.requestor.name").value("Name"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    public void shouldAddNewItemRequestWithList() throws Exception {
        ItemRequestDtoReturned returned =
                ItemRequestMapper.toItemRequestDtoReturned(itemRequest, List.of(item, item2));
        when(itemRequestService.add(any(), any())).thenReturn(returned);

        String json = objectMapper.writeValueAsString(returned);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Ручка"))
                .andExpect(jsonPath("$.requestor.id").value(1))
                .andExpect(jsonPath("$.requestor.name").value("Name"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[1].id").value(2));
    }
}
