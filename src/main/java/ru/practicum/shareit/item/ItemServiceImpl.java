package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto add(ItemDto dto, Long userId) {
        log.info("POST item request received to endpoint [/items]");
        if (!validation(dto)) {
            log.error("Validation exception for item");
            throw new ValidationException("Item is invalid");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found for id = %d", userId)));
        if (dto.getRequestId() == null) {
            return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(dto, user, null)));
        }
        return ItemMapper.toItemDto(itemRepository.save(
                ItemMapper.toItem(dto, user, itemRequestRepository.findById(dto.getRequestId())
                        .orElseThrow(() -> new NotFoundException("Request not found")))));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        log.info("PATCH item request received to endpoint [/items] with userId = {}, itemId = {}", userId, itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item not found exception for id = {}", itemId);
                    throw new NotFoundException(String.format("Item not found for id = %d", itemId));
                });
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.error("Access denied for userId = {}", userId);
            throw new AccessDenied("Access denied");
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        itemRepository.updateByNotNullFields(itemId, item.getDescription(), item.getName(), item.getAvailable());
        return ItemMapper.toItemDto(itemRepository.getById(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRDto get(Long itemId, Long userId) {
        log.info("GET item request received to endpoint [/items] with itemId = {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item not found exception for id = {}", itemId);
                    throw new NotFoundException(String.format("Item not found for id = %d", itemId));
                });
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemRDto(item, bookingRepository.findAllForItemId(itemId), commentRepository.findAllByItem_Id(itemId));
        }
        return ItemMapper.toItemRDto(item, new ArrayList<>(), commentRepository.findAllByItem_Id(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRDto> getList(Long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Incorrect Size or Num of first element");
        }
        log.info("GET item list request received to endpoint [/items] with userId = {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User not found for id = {}", userId);
            throw new NotFoundException(String.format("User not found for id = %d", userId));
        }
        List<Item> items = itemRepository.findAllByOwner_Id(userId, PageRequest.of(from / size, size));
        return items.stream().map(item ->
                        ItemMapper.toItemRDto(item, bookingRepository.findAllForItemId(item.getId()),
                                commentRepository.findAllByItem_Id(item.getId())))
                .sorted(Comparator.comparing(ItemRDto::getId))
                .collect(Collectors.toList());
    }

    private boolean validation(ItemDto dto) {
        return dto.getAvailable() != null &&
                dto.getDescription() != null && !dto.getDescription().isBlank() &&
                dto.getName() != null && !dto.getName().isBlank();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String query, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Incorrect Size or Num of first element");
        }
        log.info("GET item list request received to endpoint [/items] with query = {}", query);
        if (query.isBlank()) {
            log.warn("Empty query string for searching");
            return Collections.emptyList();
        }
        return itemRepository.search(query, PageRequest.of(from / size, size))
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addNewComment(CommentDto comment, Long userId, Long itemId) {
        log.info("POST comment request received to endpoint [/items]");
        if (bookingRepository.findAllByBookerIdAndItemIdAndEndIsBeforeAndStatusNot(userId, itemId, LocalDateTime.now(), Status.REJECTED).isEmpty()) {
            log.warn("Booking for item_id = {} by user_id = {} is not ended!", itemId, userId);
            throw new ValidationException("Booking is not ended!");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found exception for id = {}", userId);
                    throw new NotFoundException(String.format("User not found for id = %d", userId));
                });
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Item not found exception for id = {}", userId);
                    throw new NotFoundException(String.format("Item not found for id = %d", userId));
                });
        if (comment.getText().isBlank()) {
            log.error("Comment is empty!");
            throw new ValidationException("Blank comment value!");
        }
        return CommentMapper.toCommentDto(
                commentRepository.save(CommentMapper.toComment(comment, user, item, LocalDateTime.now())));
    }
}
