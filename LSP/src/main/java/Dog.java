public class Dog extends Animal implements Walkable {
    @Override
    public void makeSound() {
        System.out.println("Dog barks.");
    }

    @Override
    public void walk() {
        System.out.println("Dog is walking.");
    }
}