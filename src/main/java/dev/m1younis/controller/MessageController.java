
package dev.m1younis.controller;

import dev.m1younis.model.Message;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class for handling operations on message objects.
 */
public class MessageController {
    public static void storeMessage(Message message) {
        final Connection conn = DatabaseController.connect();
        try {
            // Optional message fields are extracted to check for nulls prior to insertion
            final String recipient = message.getRecipient(),
                             topic = message.getTopic(),
                           subject = message.getSubject();

            // `INSERT` query composed by way of a formatted string before being performed
            final Statement stmt = conn.createStatement();
            final String query = String.format(
                "INSERT INTO `messages` (`uid`, `created`, `sender`, `recipient`, `topic`, "
                + "`subject`, `contents`) VALUES ('%s', '%d', '%s', %s, %s, %s, '%s')",
                message.getHash(),
                message.getCreated(),
                message.getSender(),
                recipient == null ? "NULL" : String.format("'%s'", recipient),
                topic == null ? "NULL" : String.format("'%s'", topic),
                subject == null ? "NULL" : String.format("'%s'", subject),
                String.join("\n", message.getContents())
            );
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Message parseMessage(String message) {
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

    public static Map<String, Message> loadStoredMessages() {
        // The `LinkedHashMap` collection is chosen to store `Message` objects by way of their hash
        // over its base `HashMap` implementation and random-access counterpart `TreeMap` as
        // entries are chronologically contained or "insertion-ordered"
        final Map<String, Message> messages = new LinkedHashMap<>();
        final Connection conn = DatabaseController.connect();
        try {
            // A `SELECT` query is executed to extract all message records and columns for which a
            // `ResultSet` object is returned to be iterated over
            final Statement stmt = conn.createStatement();
            final ResultSet rs =
                stmt.executeQuery("SELECT * FROM `messages` ORDER BY `created`");

            // Table rows are mapped to `Message` objects then added within the `Map` container
            while (rs.next()) {
                final String hash = rs.getString("uid");
                messages.put(
                    hash,
                    new Message(
                        hash,
                        rs.getString("sender"),
                        rs.getString("recipient"),
                        rs.getString("topic"),
                        rs.getString("subject"),
                        rs.getLong("created"),
                        rs.getString("contents").split("\n")
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
