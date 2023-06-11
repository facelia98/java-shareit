package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDenied;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ItemDto add(ItemDto dto, Long userId) {
        log.info("POST item request received to endpoint [/items]");
        if (!validation(dto)) {
            log.error("Validation exception for item");
            throw new ValidationException("Item is invalid");
        }
        Item item = itemRepository.save(
                ItemMapper.toItem(dto, userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException(String.format("User not found for id = %d", userId)))));
        return ItemMapper.toItemDto(item);
        //tut obnovi
    }

    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        log.info("PATCH item request received to endpoint [/items] with userId = {}, itemId = {}", userId, itemId);
        Item item = itemRepository.findById(itemId).get();
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
        return ItemMapper.toItemDto(item);
    }

    public ItemRDto get(Long itemId, Long userId) {
        log.info("GET item request received to endpoint [/items] with itemId = {}", itemId);
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemRepository.getById(itemId);
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemRDto(item, bookingRepository.findAllForItemId(itemId), commentRepository.findAllByItem_Id(itemId));
        }
        return ItemMapper.toItemRDto(item, new ArrayList<>(), commentRepository.findAllByItem_Id(itemId));
    }

    public List<ItemRDto> getList(Long userId) {
        log.info("GET item list request received to endpoint [/items] with userId = {}", userId);
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
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

    public List<ItemDto> search(String query) {
        log.info("GET item list request received to endpoint [/items] with query = {}", query);
        if (query.isBlank()) {
            log.info("Empty query string for searching");
            return Collections.emptyList();
        }
        return itemRepository.search(query)
                .stream().map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }


    private void userCheck(Long userId) {
        if (userId == null) {
            log.error("UserId is empty");
            throw new ValidationException("UserId is empty");
        }
        if (userRepository.getById(userId) == null) {
            log.error("User not found exception");
            throw new NotFoundException("User not found exception");
        }
    }

    public CommentDto addNewComment(CommentDto comment, Long userId, Long itemId) {
        log.info("POST comment request received to endpoint [/items]");
        if (bookingRepository.findBookingByBookerIdAndItemIdAndEndIsBeforeAndStatusNot(userId, itemId, LocalDateTime.now(), Status.REJECTED).isEmpty()) {
            throw new ValidationException("Booking is not ended!");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found");
        }
        if (comment.getText().isBlank()) {
            throw new ValidationException("Blank comment value!");
        }
        return CommentMapper.toCommentDto(
                commentRepository.save(CommentMapper.toComment(comment, itemRepository.getById(itemId),
                        userRepository.getById(userId), LocalDateTime.now())));
    }
}
