package ru.practicum.shareit.JsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonItemWithCommentTests {

    @Autowired
    private JacksonTester<ItemDto> jsonItem;
    @Autowired
    private JacksonTester<ItemRDto> jsonItemR;

    @Test
    void testItemDtoIncoming() throws Exception {
        // test that incoming dto has correct field
        String json = "{ \"name\": \"Дрель\", \"description\": \"Описание\",  \"available\": true }";

        ItemDto expectedDto = ItemDto.builder()
                .name("Дрель").available(true)
                .description("Описание").build();
        ObjectContent<ItemDto> result = jsonItem.parse(json);
        result.assertThat().isEqualTo(expectedDto);
    }

    @Test
    void testItemDtoOutcoming() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .description("Описание")
                .requestId(12L)
                .name("Название")
                .build();
        JsonContent<ItemDto> result = jsonItem.write(dto);
        // test that outcoming dto:
        // has correct fields
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(12);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Название");
    }

    @Test
    void testItemRDtoOutcoming() throws Exception {
        LocalDateTime created = LocalDateTime.of(2020, 10, 1, 23, 0, 0);

        ItemRDto dto = ItemRDto.builder()
                .id(1L).description("Описание")
                .name("Название")
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of(CommentDto.builder().created(created).authorName("Имя").text("Текст").build()))
                .build();
        JsonContent<ItemRDto> result = jsonItemR.write(dto);
        // test that outcoming dto:
        // has correct fields, null value in dates of bookings
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.comments[0].created").isEqualTo("2020-10-01T23:00:00");
        assertThat(result).extractingJsonPathValue("$.comments[0].text").isEqualTo("Текст");
    }

}
