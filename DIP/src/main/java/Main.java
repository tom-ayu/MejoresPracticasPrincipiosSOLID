public class Main {
    public static void main(String[] args) {
        PaymentProcessor creditCardProcessor = new PaymentProcessor(new CreditCardPayment());
        creditCardProcessor.makePayment(150.0);

        PaymentProcessor payPalProcessor = new PaymentProcessor(new PayPalPayment());
        payPalProcessor.makePayment(200.0);

        PaymentProcessor cryptoProcessor = new PaymentProcessor(new CryptoPayment());
        cryptoProcessor.makePayment(350.0);
    }
}