package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoReturned add(ItemRequestDto dto, Long userId);

    List<ItemRequestDtoReturned> getAll(Long userId);

    List<ItemRequestDtoReturned> getAllFromOthers(Long requestor, PageRequest pageRequest);

    ItemRequestDtoReturned get(Long id, Long userId);
}
