class CreditCardPayment {
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
}

class PaymentProcessor {
    private CreditCardPayment payment;

    public PaymentProcessor() {
        this.payment = new CreditCardPayment(); // Dependencia directa
    }

    public void makePayment(double amount) {
        payment.processPayment(amount);
    }
}

public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();
        processor.makePayment(150.0);
    }
}