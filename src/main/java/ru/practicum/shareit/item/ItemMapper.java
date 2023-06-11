package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable()).build();
    }

    public static ItemRequestDto toItemResponseDto(Item item, List<Booking> booking) {
        Booking bookingLast = null;
        Booking bookingNext = null;
        LocalDateTime time = LocalDateTime.now();

        if (!booking.isEmpty()) {

            Optional<Booking> bookingLastOld = booking.stream()
                    .filter(b -> (b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED)))
                    .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                    .sorted(Comparator.comparing(Booking::getStart).reversed()) //reversed
                    .findFirst();

            Optional<Booking> bookingNextOld = booking.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(b -> b.getStart().isAfter(time))
                    .findFirst();
            if (bookingLastOld.isPresent()) {
                bookingLast = bookingLastOld.get();
            }
            if (bookingNextOld.isPresent()) {
                bookingNext = bookingNextOld.get();
            }

        }
        return ItemRequestDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .lastBooking(bookingLast)
                .nextBooking(bookingNext)
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner).build();
    }

    public static ItemRDto toItemRDto(Item item, List<Booking> booking, List<Comment> comments) {
        Booking bookingLast = null;
        Booking bookingNext = null;
        LocalDateTime time = LocalDateTime.now();

        if (!booking.isEmpty()) {

            Optional<Booking> bookingLastOld = booking.stream()
                    .filter(b -> (b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED)))
                    .filter(b -> (b.getStart().isBefore(time) && b.getEnd().isAfter(time)) || b.getEnd().isBefore(time))
                    .sorted(Comparator.comparing(Booking::getStart).reversed()) //reversed
                    .findFirst();

            Optional<Booking> bookingNextOld = booking.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()) && b.getStatus().equals(Status.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .filter(b -> b.getStart().isAfter(time))
                    .findFirst();
            if (bookingLastOld.isPresent()) {
                bookingLast = bookingLastOld.get();
            }
            if (bookingNextOld.isPresent()) {
                bookingNext = bookingNextOld.get();
            }

        }
        return ItemRDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .lastBooking(bookingLast == null ? null : BookingShort.builder().id(bookingLast.getId()).bookerId(bookingLast.getBooker().getId())
                        .build())
                .nextBooking(bookingNext == null ? null : BookingShort.builder().id(bookingNext.getId()).bookerId(bookingNext.getBooker().getId())
                        .build())
                .comments(comments.stream().map(comment -> CommentMapper.toCommentDto(comment)).collect(Collectors.toList()))
                .build();
    }

}
