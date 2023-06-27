package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    //addNewUser
    public ResponseEntity<Object> addUser(UserDto requestDto) {
        return post("", requestDto);
    }

    //updateUser
    public ResponseEntity<Object> updateUser(long userId, UserDto requestDto) {
        return patch("/" + userId, requestDto);
    }

    //findById
    public ResponseEntity<Object> getUser(long userId) {
        return get("/" + userId);
    }

    //getAll
    public ResponseEntity<Object> getAll() {
        return get("/");
    }

    //deleteUser
    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/" + userId);
    }
}
