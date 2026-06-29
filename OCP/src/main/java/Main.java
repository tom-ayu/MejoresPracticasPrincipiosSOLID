class NotificationService {
    public void sendNotification(String type, String message) {
        if (type.equals("Email")) {
            System.out.println("Sending Email: " + message);
        } else if (type.equals("SMS")) {
            System.out.println("Sending SMS: " + message);
        } else if (type.equals("Push")) {
            System.out.println("Sending Push Notification: " + message);
        } else {
            System.out.println("Invalid notification type!");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        service.sendNotification("Email", "Hello via Email!");
        service.sendNotification("SMS", "Hello via SMS!");
        service.sendNotification("Push", "Hello via Push Notification!");
        service.sendNotification("Fax", "Hello via Fax!");
    }
}