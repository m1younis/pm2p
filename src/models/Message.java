
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
        // A more native way of generating the current UNIX Epoch time, which can also be done via
        // the `time.Instant` class
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

    @Override
    public String toString() {
        // A `StringBuilder` object is utilised here to order mandatory and optional message
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
