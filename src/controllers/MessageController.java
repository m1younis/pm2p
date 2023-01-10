
package controllers;

import models.Message;
import java.io.FileWriter;
import java.io.IOException;

public class MessageController {
    public static final String LOCAL_MESSAGES = "messages.txt";

    public static void storeMessage(Message message, boolean append) {
        try {
            // The `append` parameter is used to distinguish between writing to the local storage
            // file depending on whether it already exists
            final FileWriter writer = new FileWriter(LOCAL_MESSAGES, append);
            writer.write(message.getBody() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
