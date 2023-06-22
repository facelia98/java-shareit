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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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
    public void shouldAddNewComment() throws Exception {
        LocalDateTime created = LocalDateTime.of(2020, 10, 1, 23, 0, 0);

        CommentDto dto = CommentDto.builder()
                .id(1L).authorName("Name").created(created).text("Text").build();
        when(itemService.addNewComment(any(), any(), any())).thenReturn(dto);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.authorName").value("Name"))
                .andExpect(jsonPath("$.text").value("Text"))
                .andExpect(jsonPath("$.created").value("2020-10-01T23:00:00"));
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
        when(itemService.search("Name", PageRequest.of(0, 10))).thenReturn(List.of(itemDto));
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

    @Test
    public void shouldReturnItemListByOwnerId() throws Exception {

        User user = User.builder().id(1L).name("Name").email("mail@mail.ru").build();
        List<ItemRDto> result = List.of(
                ItemMapper.toItemRDto(
                        ItemMapper.toItem(itemDto, user, null), List.of(), List.of()),
                ItemMapper.toItemRDto(
                        ItemMapper.toItem(itemDto2, user, null), List.of(), List.of()),
                ItemMapper.toItemRDto(
                        ItemMapper.toItem(itemDto3, user, null), List.of(), List.of())
        );
        when(itemService.getList(any(), any())).thenReturn(result);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[2].id").value(3));
    }
}
