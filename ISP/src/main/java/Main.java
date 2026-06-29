interface Device {
    void turnOn();
    void turnOff();
    void charge();
}

class Phone implements Device {
    @Override
    public void turnOn() {
        System.out.println("Phone is turning on.");
    }

    @Override
    public void turnOff() {
        System.out.println("Phone is turning off.");
    }

    @Override
    public void charge() {
        System.out.println("Phone is charging.");
    }
}

class DisposableCamera implements Device {
    @Override
    public void turnOn() {
        System.out.println("Disposable camera is turning on.");
    }

    @Override
    public void turnOff() {
        System.out.println("Disposable camera is turning off.");
    }

    @Override
    public void charge() {
        // Disposable cameras cannot be charged, but they are forced to implement this method.
        throw new UnsupportedOperationException("Disposable cameras cannot be charged.");
    }
}

public class Main {
    public static void main(String[] args) {
        Device phone = new Phone();
        Device camera = new DisposableCamera();

        phone.turnOn();
        phone.charge();

        camera.turnOn();
        camera.charge(); // Excepción: UnsupportedOperationException
    }
}