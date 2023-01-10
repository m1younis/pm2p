
package controllers;

import models.Message;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MessageController {
    public static final String LOCAL_MESSAGES = "messages.txt";

    public static void storeMessage(Message message, boolean append) {
        try {
            // The `append` parameter is used to distinguish between writing to the local storage
            // file depending on whether it already exists
            final FileWriter writer = new FileWriter(MessageController.LOCAL_MESSAGES, append);
            writer.write(message.getBody() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Message parseStoredMessage(String message) {
        // Variables declared for each of the `Message` object headers
        String hash = null,
             sender = null,
          recipient = null,
              topic = null,
            subject = null;
        long created = 0;
        int contents = 0;                // Stores the index at which the message contents begin

        // The text corresponding to the message body is split into individual lines, from which
        // the following loop identifies each of the present message headers and contents
        final String[] body = message.split("\n");
        for (int i = 0; i < body.length; i++) {
            final String line = body[i];
            if (line.startsWith("Message-uid: SHA-256")) {
                final String[] meta = line.split("\\s+");
                if (meta.length == 3)
                    hash = meta[2];
            } else if (line.startsWith("Contents:")) {
                contents = i + 1;
                break;
            } else {
                final String sub = line.substring(line.indexOf(':') + 2);
                if (line.startsWith("Created:"))
                    created = Long.parseLong(sub);
                else if (line.startsWith("From:"))
                    sender = sub;
                else if (line.startsWith("To:"))
                    recipient = sub;
                else if (line.startsWith("Topic:"))
                    topic = sub;
                else if (line.startsWith("Subject:"))
                    subject = sub;
            }
        }

        return new Message(
            hash,
            sender,
            recipient,
            topic,
            subject,
            created,
            Arrays.copyOfRange(body, contents, body.length)
        );
    }
}
