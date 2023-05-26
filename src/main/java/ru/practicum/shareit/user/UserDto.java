package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private List<Long> items;
}
