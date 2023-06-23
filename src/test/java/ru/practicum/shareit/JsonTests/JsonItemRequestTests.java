package ru.practicum.shareit.JsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestDtoReturned;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonItemRequestTests {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequest;
    @Autowired
    private JacksonTester<ItemRequestDtoReturned> jsonItemRequestReturned;


    @Test
    void testItemRequestDtoIncoming() throws Exception {
        // test that incoming dto has correct field
        String json = "{ \"description\": \"Описание\" }";

        ItemRequestDto expectedDto = ItemRequestDto.builder()
                .description("Описание").build();
        ObjectContent<ItemRequestDto> result = jsonItemRequest.parse(json);
        result.assertThat().isEqualTo(expectedDto);
    }

    @Test
    void testItemRequestDtoOutcoming() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Описание")
                .requestor(new User(2L, "e@e.com", "name"))
                .build();
        JsonContent<ItemRequestDto> result = jsonItemRequest.write(dto);
        // test that incoming dto:
        // has correct fields
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo("e@e.com");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
    }

    @Test
    void testItemRequestDtoReturnedOutcoming() throws Exception {
        LocalDateTime created = LocalDateTime.of(2020, 10, 1, 23, 0, 0);

        ItemRequestDtoReturned dto = ItemRequestDtoReturned.builder()
                .id(1L).description("Описание").created(created).items(null)
                .requestor(new User(2L, "e@e.com", "name")).build();
        JsonContent<ItemRequestDtoReturned> result = jsonItemRequestReturned.write(dto);
        // test that incoming dto:
        // has correct fields, null value in items list
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo("e@e.com");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathValue("$.items").isNull();
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2020-10-01T23:00:00");
    }

}
