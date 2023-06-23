package ru.practicum.shareit.ControllersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatus;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.status.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    @Autowired
    private final ObjectMapper objectMapper;
    @MockBean
    private final BookingService bookingService;

    Item item = Item.builder().id(1L).name("Item").description("Desc").available(true).build();
    User user = User.builder().id(1L).name("Name").email("mail@mail.ru").build();
    LocalDateTime start = LocalDateTime.now().plusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(2);
    Booking booking = new Booking(1L, item, start, end, user, Status.WAITING);
    Booking updatedBooking = new Booking(1L, item, start, end, user, Status.APPROVED);


    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnBooking() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(BookingMapper.toBookingReturningDto(booking));
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1));
    }

    @Test
    public void shouldAddNewBooking() throws Exception {
        when(bookingService.save(any(), any()))
                .thenReturn(BookingMapper.toBookingReturningDto(booking));
        String json = "{ \"itemId\": 2, \"start\": \"2020-10-01T23:00:00\", \"end\": \"2020-10-02T23:00:00\" }";
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1));
    }

    @Test
    public void shouldUpdateBooking() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(BookingMapper.toBookingReturningDto(updatedBooking));
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void shouldReturnAllForUserBooking404() throws Exception {
        when(bookingService.findAllByUserId(anyLong(), anyString(), any()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get("/bookings/state=ALL&from=1&size=10")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnAllForUserBooking4xx() throws Exception {
        when(bookingService.findAllByUserId(anyLong(), anyString(), any()))
                .thenThrow(ValidationException.class);
        mockMvc.perform(get("/bookings/state=ALL&from=-1&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnAllForOwnerBooking4xx() throws Exception {
        when(bookingService.findAllForOwner(anyLong(), anyString(), any()))
                .thenThrow(UnsupportedStatus.class);
        mockMvc.perform(get("/bookings/owner?state=KEK&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnAllForOwnerBooking() throws Exception {
        when(bookingService.findAllForOwner(anyLong(), anyString(), any()))
                .thenReturn(List.of(BookingMapper.toBookingReturningDto(booking)));
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").hasJsonPath())
                .andExpect(jsonPath("$[0].end").hasJsonPath())
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1));
    }

    @Test
    public void shouldReturnAllForUserBooking() throws Exception {
        when(bookingService.findAllByUserId(anyLong(), anyString(), any()))
                .thenReturn(List.of(BookingMapper.toBookingReturningDto(booking)));
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].start").hasJsonPath())
                .andExpect(jsonPath("$[0].end").hasJsonPath())
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1));
    }
}
