package ru.practicum.shareit.JpaTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Test
    public void isAvailableForBookingTest() {

    }
}
