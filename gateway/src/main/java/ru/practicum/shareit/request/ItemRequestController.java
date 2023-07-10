package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ItemRequestDto dto) {
        return itemRequestClient.addRequest(userId, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("id") Long id) {
        return itemRequestClient.getRequest(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAll(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllFromOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemRequestClient.getAllFromOthers(userId, from, size);
    }
}
