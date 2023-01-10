
import controllers.MessageController;
import models.Message;
import java.io.File;
import java.io.IOException;

/**
 * The project's entry point, responsible for launching the application.
 */
public class App {
    public static void main(String[] args) {
        // The testing `Message` object is initialised to be stored (given the local storage file
        // doesn't already exist) and displayed
        final Message message = new Message(
            "5103d1029e1c05d5e0a72833fbe6ed727266f1f333a64c3af2243ae6c1918b65",
            "@m1younis",
            null,
            "#testing",
            null,
            1672766344,
            "This, is, a, message, for, testing!".split(", ")
        );

        try {
            if (new File(MessageController.LOCAL_MESSAGES).createNewFile()) {
                MessageController.storeMessage(message, false);
                System.out.printf(
                    "`%s` created with Message-uid: SHA-256 %s\n",
                    MessageController.LOCAL_MESSAGES,
                    message.getHash()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(message.getBody());
    }
}
