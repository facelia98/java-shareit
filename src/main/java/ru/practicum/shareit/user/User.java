package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {
    private Long id;
    private String email;
    private String name;
    private List<Long> items;
}
