
package controllers;

import models.Message;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedHashMap;

/**
 * A class for handling operations on message objects.
 */
public class MessageController {
    public static final String LOCAL_MESSAGES = "messages.txt";

    public static void storeMessage(Message message, boolean append) {
        try {
            // The `append` parameter is used to distinguish between writing to the local storage
            // file depending on whether it already exists
            final FileWriter writer = new FileWriter(MessageController.LOCAL_MESSAGES, append);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Message parseStoredMessage(String message) {
        // Variables declared for each of the stored `Message` object's headers
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

    public static LinkedHashMap<String, Message> loadStoredMessages() {
        // The `LinkedHashMap` collection is chosen to store `Message` objects by way of their hash
        // over its base `HashMap` implementation and random-access counterpart `TreeMap` as
        // entries are chronologically contained or "insertion-ordered"
        final LinkedHashMap<String, Message> messages = new LinkedHashMap<>();

        try {
            final StringBuilder sb = new StringBuilder();
            final Scanner in = new Scanner(new File(MessageController.LOCAL_MESSAGES));
            while (in.hasNextLine()) {
                final String line = in.nextLine();
                sb.append(line + "\n");
                // Since locally stored `Message` objects are separated by a blank line, the
                // `StringBuilder` object used when parsing them must be reset before moving onto
                // the next
                if (line.isBlank()) {
                    final Message message = MessageController.parseStoredMessage(sb.toString());
                    messages.put(message.getHash(), message);
                    sb.setLength(0);
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
