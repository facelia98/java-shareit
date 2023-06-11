package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.status.Status;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional
    @Query("update Booking b set b.status = ?2 where b.id = ?1")
    void updateByStatus(Long bookingId, String status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findBookingByBookerIdAndItemIdAndEndIsBeforeAndStatusNot(Long bookerId, Long itemId, LocalDateTime instant, Status status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime instant, LocalDateTime instant1);

    List<Booking> findBookingByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime instant);

    List<Booking> findBookingByBookerIdAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime instant);

    List<Booking> findBookingByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

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

    @Query("SELECT b FROM Booking AS b JOIN b.item AS i " +
            "WHERE i.owner.id = ?1 AND b.start <= ?2 AND b.end >= ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllForOwnerCurrent(Long userId, LocalDateTime instant);
}
