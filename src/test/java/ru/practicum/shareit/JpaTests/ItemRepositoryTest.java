package ru.practicum.shareit.JpaTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    Item item;
    Item item2;
    User user;
    User user2;
    ItemRequest request;
    @Autowired
    private ItemRepository repository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        request = itemRequestRepository.save(new ItemRequest(2L, "Des", user2, LocalDateTime.now()));
        user = userRepository.save(User.builder().name("Name").email("e@e.com").build());
        user2 = userRepository.save(User.builder().name("Name2").email("e2@e.com").build());
        item = repository.save(Item.builder().name("Item").owner(user).description("Desc").available(true).build());
        item2 = repository.save(Item.builder().name("Item2").owner(user).description("Desc2").available(true)
                .request(request).build());
    }

    @Test
    public void findAllForItemIdTestOKWithoutUnavailable() {
        List<Item> list = repository.search("Item", PageRequest.of(0, 1));
        assertEquals(List.of(item), list);
    }

    @Test
    public void findAllByOwner_IdTestOK() {
        assertEquals(repository.findAllByOwner_Id(user.getId(), PageRequest.ofSize(2))
                , List.of(item, item2));
        assertEquals(repository.findAllByOwner_Id(user.getId(), PageRequest.ofSize(1))
                , List.of(item));
        assertEquals(repository.findAllByOwner_Id(user2.getId(), PageRequest.ofSize(1))
                , List.of());
    }

    @Test
    public void findAllByRequest_IdTestOK() {
        assertEquals(repository.findAllByRequest_Id(2L)
                , List.of());
        assertEquals(repository.findAllByRequest_Id(request.getId())
                , List.of(item2));
    }
}
