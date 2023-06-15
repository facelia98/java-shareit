package ru.practicum.shareit.JsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonBookingTests {
    @Autowired
    private JacksonTester<BookingDto> jsonBooking;
    @Autowired
    private JacksonTester<BookingReturningDto> jsonBookingReturning;

    @Test
    void testBookingReturningDtoOutcoming() throws Exception {

        LocalDateTime start = LocalDateTime.of(2020, 10, 1, 23, 0, 0);
        LocalDateTime end = start.plusDays(1);
        User user = new User(1L, "e@e.com", "Name");
        Item item = Item.builder()
                .id(2L).available(true).description("Описание").name("Название")
                .owner(user)
                .request(null)
                .build();
        BookingReturningDto dto = BookingReturningDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .status(Status.WAITING)
                .build();

        JsonContent<BookingReturningDto> result = jsonBookingReturning.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.item.request").isNull();
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2020-10-01T23:00:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2020-10-02T23:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testBookingDtoIncoming() throws Exception {
        // test that incoming bookingDto has correct fields
        String json = "{ \"itemId\": 2, \"start\": \"2020-10-01T23:00:00\", \"end\": \"2020-10-02T23:00:00\" }";
        LocalDateTime start = LocalDateTime.of(2020, 10, 1, 23, 0, 0);
        LocalDateTime end = start.plusDays(1);

        BookingDto expectedDto = BookingDto.builder()
                .id(null).itemId(2L).start(start).end(end).status(Status.WAITING).build();
        ObjectContent<BookingDto> result = jsonBooking.parse(json);
        result.assertThat().isEqualTo(expectedDto);

    }

    @Test
    void testBookingDtoOutcoming() throws Exception {
        // test that outcoming bookingDto:
        // date-format fields formatted by pattern,
        // booking hasn't id and status
        LocalDateTime start = LocalDateTime.of(2020, 10, 1, 23, 0, 0);
        LocalDateTime end = start.plusDays(1);
        BookingDto bookingDto = BookingDto.builder()
                .id(null)
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingDto> result = jsonBooking.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2020-10-01T23:00:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2020-10-02T23:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isNull();
    }
}
