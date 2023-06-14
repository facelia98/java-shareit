package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.status.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select (count(b) > 0) from Booking b " +
            "where b.item.id = ?3 and b.status = 'APPROVED' and b.start <= ?2 and b.end >= ?1")
    boolean isAvailableForBooking(LocalDateTime start, LocalDateTime end, Long itemId);

    @Query("select case when ?2 > ?1 AND ?3 < ?1 then true else false end from Item")
    boolean checkForBooking(LocalDateTime start, LocalDateTime end, LocalDateTime now);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndItemIdAndEndIsBeforeAndStatusNot(Long bookerId, Long itemId, LocalDateTime instant, Status status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime instant, LocalDateTime instant1, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime instant, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime instant, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, PageRequest pageRequest);

    @Query("select booking from Booking booking where booking.item.id in " +
            "(select item.id from Item item where item.id =?1) order by booking.start desc")
    List<Booking> findAllForItemId(Long itemId);

    @Query("select booking from Booking booking where booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwner(Long ownerId, PageRequest pageRequest);

    @Query("select booking from Booking booking where booking.status = ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwner(Long ownerId, Status status, PageRequest pageRequest);

    @Query("select booking from Booking booking where booking.start > ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime instant, PageRequest pageRequest);

    @Query("select booking from Booking booking where booking.end < ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwnerAndBeforeInstant(Long ownerId, LocalDateTime instant, PageRequest pageRequest);

    @Query("select booking from Booking booking where booking.start < ?2 and booking.end > ?2 and " +
            "booking.item.id in " +
            "(select item.id from Item item where item.owner.id =?1) order by booking.start desc")
    List<Booking> findAllForOwnerCurrent(Long userId, LocalDateTime instant, PageRequest pageRequest);
}
