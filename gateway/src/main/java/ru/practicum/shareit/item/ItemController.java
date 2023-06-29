package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid ItemDto item) {
        return itemClient.addItem(userId, item);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody ItemDto item) {
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam("text") String query,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemClient.search(userId, query, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("id") Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsListForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemClient.getItemsForOwner(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid CommentDto comment,
                                                @PathVariable("itemId") Long itemId) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
