package ua.pr7;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private PlaceService placeService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingService bookingService;

    //Сценарій, де користувач успішно зарезервував місце
    @Test
    void test_book_success() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(true);
        String result = bookingService.book("Room№1", 200);
        assertEquals("Place booked", result);
    }

    //Сценарій, де користувач не зміг зарезервувати місце, оскільки воно було вже зарезервоване
    @Test
    void test_bookPlace_fail() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(true);
        String result = bookingService.book("Room№1", 200);
        assertEquals("Place is already booked", result);
    }

    //Сценарій, де користувач не зміг оплатити бронювання
    @Test
    void test_pay_fail() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(false);
        String result = bookingService.book("Room№1", 200);
        assertEquals("Payment failed", result);
    }

    @Test
    void test_verify() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(true);
        bookingService.book("Room№1", 200);
        verify(placeService).bookPlace("Room№1");
        verify(notificationService).sendNotification("Room№1 booked");
    }

    @Test
    void test_verify_never() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(true);
        bookingService.book("Room№1", 200);
        verify(placeService, never()).bookPlace("Room№1");
        verify(notificationService, never()).sendNotification("Room№1 booked");
    }

    @Test
    void test_verify_times() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(true);
        bookingService.book("Room№1", 200);
        verify(placeService, times(1)).bookPlace("Room№1");
        verify(notificationService, times(1)).sendNotification("Room№1 booked");
    }

    @Test
    void test_soft_assertions() {
        when(placeService.isBookedPlace("Room№1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(true);
        String result = bookingService.book("Room№1", 200);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(result).isNotNull();
        softAssertions.assertThat(result).isEqualTo("Place booked");
        softAssertions.assertAll();
    }

    @Test
    void test_assertions_list() {
        List<String> rooms = List.of("Room№1", "Room№2", "Room№3");
        assertThat(rooms).isNotEmpty();
        assertThat(rooms).isNotNull();
        assertThat(rooms).hasSize(3);
        assertThat(rooms).doesNotHaveDuplicates();
        assertThat(rooms).containsExactly("Room№1", "Room№2", "Room№3");
        assertThat(rooms).doesNotContain("Room№4");
    }

    @Test
    void test_pit_survived() {
        when(placeService.isBookedPlace("Room1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(false);
        assertThat(bookingService.book("Room1", 200)).isEqualTo("Payment failed");
    }

    @Test
    void test_pit_killed() {
        assertThat(bookingService.book(null, 200)).isEqualTo("Invalid place");
        assertThat(bookingService.book("", 200)).isEqualTo("Invalid place");
        assertThat(bookingService.book("Room1", 0)).isEqualTo("Invalid amount");

        when(placeService.isBookedPlace("Room1")).thenReturn(false);
        when(paymentService.pay(200)).thenReturn(false);
        assertThat(bookingService.book("Room1", 200)).isEqualTo("Payment failed");
    }
}