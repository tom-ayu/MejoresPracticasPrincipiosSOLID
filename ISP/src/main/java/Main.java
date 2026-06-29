public class Main {
    public static void main(String[] args) {
        Phone phone = new Phone();
        DisposableCamera camera = new DisposableCamera();

        phone.turnOn();
        phone.charge();
        phone.turnOff();

        camera.turnOn();
        camera.turnOff();
    }
}