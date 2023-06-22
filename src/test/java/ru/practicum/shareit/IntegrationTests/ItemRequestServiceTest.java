package ru.practicum.shareit.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        itemRequestService =
                new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void test() {
        User owner = User.builder().id(1L).name("Owner").email("owner@mail.ru").build();
        User requestor = User.builder().id(2L).name("Booker").email("booker@mail.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(10L).requestor(requestor).created(LocalDateTime.now()).description("Item")
                .build();
        Item item = Item.builder()
                .id(1L).owner(owner).name("Item").description("Desc").available(true).request(itemRequest)
                .build();

        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(itemRepository.findAllByRequest_Id(10L))
                .thenReturn(List.of(item));
        Mockito.when(itemRequestRepository.findAllByRequestor_IdNot(2L, PageRequest.of(0, 10)))
                .thenReturn((List.of(itemRequest)));

        assertEquals(itemRequestService.getAllFromOthers(2L, PageRequest.of(0, 10)), List.of(ItemRequestMapper
                .toItemRequestDtoReturned(itemRequest, List.of(ItemMapper.toItemDto(item)))));
    }
}
