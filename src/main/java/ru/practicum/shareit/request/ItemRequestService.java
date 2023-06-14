package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoReturned add(ItemRequestDto dto, Long userId);

    List<ItemRequestDtoReturned> getAll(Long userId);

    List<ItemRequestDtoReturned> getAllFromOthers(Long requestor, int from, int size);

    ItemRequestDtoReturned get(Long id, Long userId);
}
