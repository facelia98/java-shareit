package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoReturned addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody ItemRequestDto dto) {
        return itemRequestService.add(dto, userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDtoReturned getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("id") Long id) {
        return itemRequestService.get(id, userId);
    }

    @GetMapping
    public List<ItemRequestDtoReturned> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoReturned> getAllFromOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(value = "from", defaultValue = "0") int from,
                                                         @RequestParam(value = "size", defaultValue = "20") int size) {
        return itemRequestService.getAllFromOthers(userId, PageRequest.of(from / size, size));
    }
}
