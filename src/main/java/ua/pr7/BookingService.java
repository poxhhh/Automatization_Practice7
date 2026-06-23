package ua.pr7;

public class BookingService {
    private PaymentService paymentService;
    private PlaceService placeService;
    private NotificationService notificationService;

    public BookingService(PaymentService paymentService, PlaceService placeService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.placeService = placeService;
        this.notificationService = notificationService;
    }

    public String book(String place, double amount) {
        if(place == null || place.isEmpty()) return "Invalid place";
        if(amount <= 0) return "Invalid amount";

        if(placeService.isBookedPlace(place)) {
            return "Place is already booked";
        }
        if(!paymentService.pay(amount)) {
            return  "Payment failed";
        }
        placeService.bookPlace(place);
        notificationService.sendNotification(place + " booked");
        return "Place booked";
    }
}
