package ru.practicum.shareit.request;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                //itemRequest.getCreated(),
                itemRequest.getRequestor());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                //itemRequestDto.getCreated(),
                itemRequestDto.getRequestor());
    }
}
