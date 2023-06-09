package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDtoReturned toItemRequestDtoReturned(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDtoReturned(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                items);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now());
    }
}
