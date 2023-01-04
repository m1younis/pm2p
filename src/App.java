
import models.Message;

/**
 * The project's entry point, responsible for launching the application.
 */
public class App {
    public static void main(String[] args) {
        // A test `Message` object is created and displayed
        final Message message = new Message(
            "5103d1029e1c05d5e0a72833fbe6ed727266f1f333a64c3af2243ae6c1918b65",
            "@m1younis",
            null,
            "#testing",
            null,
            1672766344,
            "This, is, a, message, for, testing!".split(", ")
        );

        System.out.println(message.formattedBody('r'));
    }
}
