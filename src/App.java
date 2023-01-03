
import models.Message;

/**
 * The project's entry point, responsible for launching the application.
 */
public class App {
    public static void main(String[] args) {
        // A test `Message` object is created and displayed
        final Message message = new Message(
            "@m1younis",
            null,
            "#testing",
            null,
            "This, is, a, message, for, testing!".split(", ")
        );

        System.out.println(
            message.getCreated() + "\n" +
            message.getSender() + "\n" +
            message.getRecipient() + "\n" +
            message.getTopic() + "\n" +
            message.getSubject() + "\n" +
            message.getSize()
        );
    }
}
