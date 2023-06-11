package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.status.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndItemIdAndEndIsBeforeAndStatusNot(Long bookerId, Long itemId, LocalDateTime instant, Status status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime instant, LocalDateTime instant1);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime instant);

    List<Booking> findAllByBookerIdAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime instant);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    @Query("select booking from Booking booking where booking.item.id in " +
            "(select item.id from Item item where item.id =?1) order by booking.start desc")
    List<Booking> findAllForItemId(Long itemId);

    @Query("select booking from Booking booking where booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwner(Long ownerId);

    @Query("select booking from Booking booking where booking.status = ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwner(Long ownerId, Status status);

    @Query("select booking from Booking booking where booking.start > ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime instant);

    @Query("select booking from Booking booking where booking.end < ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwnerAndBeforeInstant(Long ownerId, LocalDateTime instant);

    @Query("select booking from Booking booking where booking.start < ?2 and booking.end > ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwnerCurrent(Long userId, LocalDateTime instant);
}
