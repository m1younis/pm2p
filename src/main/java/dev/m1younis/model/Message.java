
package dev.m1younis.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

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
        // A more native way of generating the current Unix Epoch time, which can also be done via
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

    // The copy constructor defined below is for initialising persistently stored `Message` objects
    public Message(String hash,
                   String sender,
                   String recipient,
                   String topic,
                   String subject,
                   long created,
                   String[] contents) {
        this.hash = hash;
        this.sender = sender;
        this.recipient = recipient;
        this.topic = topic;
        this.subject = subject;
        this.created = created;
        this.contents = contents;
    }

    private String build(boolean append) {
        // A `StringBuilder` object is utilised here to position mandatory and optional message
        // headers accordingly
        final StringBuilder sb = new StringBuilder(String.format("Created: %d\n", this.created))
            .append(String.format("From: %s\n", this.sender));

        // Optional headers are dealt with prior to the message contents
        if (this.recipient != null)
            sb.append(String.format("To: %s\n", this.recipient));
        if (this.topic != null)
            sb.append(String.format("Topic: %s\n", this.topic));
        if (this.subject != null)
            sb.append(String.format("Subject: %s\n", this.subject));

        // Contents appended last
        sb.append(String.format("Contents: %d\n", this.contents.length))
            .append(String.join("\n", this.contents));

        // `append` determines whether a trailing line is added
        return append ? sb.append("\n").toString() : sb.toString();
    }

    private String generateHash() {
        try {
            // SHA-256 is the chosen hashing function - each byte in the incomplete message body
            // (formed by invoking the `build` method) is hashed, converted to its hex equivalent
            // then stored within a `StringBuilder` object, resulting in the corresponding sum
            final StringBuilder sb = new StringBuilder();
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (byte b : md.digest(this.build(true).getBytes(StandardCharsets.UTF_8))) 
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

    public String[] getContents() {
        return this.contents;
    }

    @Override
    public String toString() {
        return new StringJoiner("\n")
            .add(String.format("Message-uid: SHA-256 %s", this.hash))
            .add(this.build(false))
            .toString();
    }
}
