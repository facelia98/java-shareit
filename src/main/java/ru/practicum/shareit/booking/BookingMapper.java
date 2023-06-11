package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturningDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookingReturningDto toBookingReturningDto(Booking booking) {
        return BookingReturningDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .item(item)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .booker(booker)
                .build();
    }
}
