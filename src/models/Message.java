
package models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A class representing the app's message objects.
 */
public class Message {
    private String hash,
                   sender,
                   recipient,
                   topic,
                   subject;

    private long created;
    private String[] contents;

    public Message(String sender,
                   String recipient,
                   String topic,
                   String subject,
                   String[] contents) {
        // A more native way of generating the current UNIX Epoch time, which can also be done via
        // the `time.Instant` class
        this.created = System.currentTimeMillis() / 1000;
        this.sender = sender;
        this.recipient = recipient;
        this.topic = topic;
        this.subject = subject;
        this.contents = contents;
        // The unique SHA-256 sum is generated only once the message body is initialised, with hash
        // results matching those produced at https://emn178.github.io/online-tools/sha256.html
        this.hash = this.generateHash();
    }

    private String generateHash() {
        try {
            final StringBuilder sb = new StringBuilder();
            final MessageDigest algo = MessageDigest.getInstance("SHA-256");
            for (byte b : algo.digest(this.toString().getBytes(StandardCharsets.UTF_8)))
                sb.append(String.format("%02x", b));

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getHash() {
        return this.hash;
    }

    public long getCreated() {
        return this.created;
    }

    public String getSender() {
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getSubject() {
        return this.subject;
    }

    public int getSize() {
        return this.contents.length;
    }

    @Override
    public String toString() {
        // A `StringBuilder` object is utilised here to position mandatory and optional message
        // headers accordingly
        final StringBuilder sb = new StringBuilder(
            "Created: " + this.created + "\nFrom: " + this.sender + "\n"
        );

        // Optional headers are dealt with prior to the message contents, which are always required
        if (this.recipient != null)
            sb.append("To: " + this.recipient + "\n");
        if (this.topic != null)
            sb.append("Topic: " + this.topic + "\n");
        if (this.subject != null)
            sb.append("Subject: " + this.subject + "\n");

        sb.append("Contents: " + this.contents.length + "\n");
        for (String line : this.contents)
            sb.append(line + "\n");

        return sb.toString();
    }
}
