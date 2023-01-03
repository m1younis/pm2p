
package models;

/**
 * A class representing the app's message objects.
 */
public class Message {
    private String sender,
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
        // A more native way of generating the current UNIX Epoch time,
        // which can also be done via the `time.Instant` class
        this.created = System.currentTimeMillis() / 1000;
        this.sender = sender;
        this.recipient = recipient;
        this.topic = topic;
        this.subject = subject;
        this.contents = contents;
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
}
