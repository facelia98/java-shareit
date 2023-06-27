package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    //addNewItem
    public ResponseEntity<Object> addItem(long userId, ItemDto requestDto) {
        return post("", userId, requestDto);
    }

    //updateBooking
    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }

    //findById
    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    //findAllByUserId
    public ResponseEntity<Object> getItemsForOwner(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    //search
    public ResponseEntity<Object> search(long userId, String query, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "query", query,
                "from", from,
                "size", size
        );
        return get("?text={query}", userId, parameters);
    }

    //addNewItem
    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }
}
