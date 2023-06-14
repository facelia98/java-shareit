package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoReturned add(ItemRequestDto dto, Long userId) {
        log.info("POST itemRequest request received to endpoint [/requests]");
        if (dto.getDescription().isBlank()) {
            throw new ValidationException("Empty description!");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found for id = {}", userId);
            throw new NotFoundException(String.format("User not found exception for id = %d", userId));
        });
        dto.setRequestor(user);
        return ItemRequestMapper.toItemRequestDtoReturned(
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(dto)), null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoReturned> getAll(Long userId) {
        log.info("GET itemRequest list for user request received to endpoint [/requests]");
        if (!userRepository.existsById(userId)) {
            log.error("User not found for id = {}", userId);
            throw new NotFoundException(String.format("User not found exception for id = %d", userId));
        }
        return itemRequestRepository.findAllByRequestor_Id(userId).stream()
                .map(itemRequest ->
                    ItemRequestMapper.toItemRequestDtoReturned(itemRequest, itemRepository.findAllByRequest_Id(itemRequest.getId())
                            .stream().map(ItemMapper::toItemDto)
                            .collect(Collectors.toList()))
                )
                .sorted(Comparator.comparing(ItemRequestDtoReturned::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoReturned> getAllFromOthers(Long requestor, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Incorrect Size or Num of first element");
        }
        return itemRequestRepository.findAllByRequestor_IdNot(requestor, PageRequest.of(from / size, size)).stream()
                .map(itemRequest ->
                        ItemRequestMapper.toItemRequestDtoReturned(itemRequest, itemRepository.findAllByRequest_Id(itemRequest.getId())
                                .stream().map(ItemMapper::toItemDto)
                                .collect(Collectors.toList()))
                )
                .sorted(Comparator.comparing(ItemRequestDtoReturned::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoReturned get(Long id, Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User not found for id = {}", userId);
            throw new NotFoundException(String.format("User not found exception for id = %d", userId));
        }
        return ItemRequestMapper.toItemRequestDtoReturned(itemRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Item request not found for id = {}", id);
                    throw new NotFoundException("Item request not found");
                }), itemRepository.findAllByRequest_Id(id).stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
    }
}
