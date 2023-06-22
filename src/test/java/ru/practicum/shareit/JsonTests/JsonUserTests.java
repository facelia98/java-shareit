package ru.practicum.shareit.JsonTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class JsonUserTests {
    @Autowired
    private JacksonTester<UserDto> jsonUser;

    @Test
    void testUserDtoIncoming() throws Exception {
        // test that incoming dto has correct field
        String json = "{ \"name\": \"user\", \"email\": \"facelia98@mail.ru\" }";

        UserDto expectedDto = UserDto.builder()
                .name("user")
                .email("facelia98@mail.ru").build();
        ObjectContent<UserDto> result = jsonUser.parse(json);
        result.assertThat().isEqualTo(expectedDto);
    }

    @Test
    void testItemDtoOutcoming() throws Exception {
        UserDto outcomingDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("facelia98@mail.ru").build();
        JsonContent<UserDto> result = jsonUser.write(outcomingDto);
        // test that outcoming dto:
        // has correct fields
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("facelia98@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
    }
}
