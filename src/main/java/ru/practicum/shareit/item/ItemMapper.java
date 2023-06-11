package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable()).build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner).build();
    }

    public static ItemRDto toItemRDto(Item item, List<Booking> bookings, List<Comment> comments) {
        LocalDateTime time = LocalDateTime.now();
        Booking prev = bookings.stream()
                .filter(b -> (b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED)))
                .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                .sorted(Comparator.comparing(Booking::getStart).reversed()).findFirst().orElse(null);
        Booking next = bookings.stream()
                .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                .sorted(Comparator.comparing(Booking::getStart)).filter(b -> b.getStart().isAfter(time))
                .findFirst().orElse(null);
        return ItemRDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .lastBooking(prev == null ? null :
                        BookingShort.builder().id(prev.getId()).bookerId(prev.getBooker().getId()).build())
                .nextBooking(next == null ? null :
                        BookingShort.builder().id(next.getId()).bookerId(next.getBooker().getId()).build())
                .comments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()))
                .build();
    }
}
