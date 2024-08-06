
package dev.m1younis.view;

import dev.m1younis.controller.ClientController;
import dev.m1younis.controller.MessageController;
import dev.m1younis.model.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * The class responsible for initialising and displaying the app's main view.
 */
public class MainView extends BaseView {
    // Text fields declared
    private static final JTextField IDENTIFIER_FIELD = new JTextField(),
                                    IP_ADDRESS_FIELD = new JTextField(),
                                      PORT_NUM_FIELD = new JTextField(),
                                        SENDER_FIELD = new JTextField(),
                                     RECIPIENT_FIELD = new JTextField(),
                                         TOPIC_FIELD = new JTextField(),
                                       SUBJECT_FIELD = new JTextField(),
                                       REQUEST_FIELD = new JTextField();

    // Text areas declared
    private static final JTextArea CONTENTS_AREA = new JTextArea(),
                                   ACTIVITY_AREA = new JTextArea();

    // Buttons declared
    private static final JButton CLEAR_INFO_BUTTON = new JButton("Clear"),
                                    CONNECT_BUTTON = new JButton("Connect"),
                                 DISCONNECT_BUTTON = new JButton("Disconnect"),
                             CLEAR_CONTENTS_BUTTON = new JButton("Clear"),
                            STORED_MESSAGES_BUTTON = new JButton("View All"),
                             CREATE_MESSAGE_BUTTON = new JButton("Create"),
                              CLEAR_REQUEST_BUTTON = new JButton("Clear"),
                               SEND_REQUEST_BUTTON = new JButton("Send");

    // Regex for validating connection info defined
    private static final String IPv4_ADDRESS_REGEX =
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
                                    PORT_NUM_REGEX = "[0-9]{1,5}";

    // Activity text area datetime formatting defined
    private static final SimpleDateFormat ACTIVITY_DATETIME_FORMAT =
        new SimpleDateFormat("dd MMM yyyy, HH:mm:ss");

    private ClientController controller;        // Client handler instance

    public MainView() {
        super("pm2p", 1226, 586);

        this.controller = new ClientController(this);      // Client handler startup

        // Connection panel elements defined
        this.addLabel(new JLabel("Identifier"), true, 74, 61, 80, 22);
        this.addLabel(new JLabel("IP Address"), true, 74, 91, 80, 22);
        this.addLabel(new JLabel("Port"), true, 74, 119, 80, 22);

        this.addTextField(IDENTIFIER_FIELD, true, 166, 63, 150, 22);
        this.addTextField(IP_ADDRESS_FIELD, true, 166, 91, 150, 22);
        this.addTextField(PORT_NUM_FIELD, true, 166, 119, 150, 22);

        this.addButton(CLEAR_INFO_BUTTON, true, 102, 161, 100, 30);
        this.addButton(CONNECT_BUTTON, true, 219, 161, 100, 30);
        this.addButton(DISCONNECT_BUTTON, false, 336, 161, 100, 30);

        CLEAR_INFO_BUTTON.addActionListener(l -> this.clearConnectionPanel());

        CONNECT_BUTTON.addActionListener(l -> {
            // Connection details validated once the "Connect" button is submitted
            if (this.connectionInfoSupplied()) {
                final String host = IP_ADDRESS_FIELD.getText(),
                    iport = PORT_NUM_FIELD.getText();
                if (this.connectionInfoIsValid(host, iport)) {
                    // Supplied info is recorded after validation and used in establishing the
                    // connection to a peer
                    final String identifier = IDENTIFIER_FIELD.getText().strip();
                    final int port = Integer.parseInt(iport);
                    if (port >= 1 && port <= 65535) {
                        try {
                            this.controller.connect(identifier, host, port);
                        } catch (Exception e) {
                            this.showMessageDialog(
                                "Error occurred trying to establish a connection",
                                2
                            );
                            e.printStackTrace();
                        }
                    } else {
                        this.showMessageDialog(
                            "Port number must be between 1 and 65535 inclusive",
                            2
                        );
                    }
                } else
                    this.showMessageDialog("Invalid IP address and/or port number submitted", 2);
            } else
                this.showMessageDialog("Please fill in all the connection fields", 2);
        });

        DISCONNECT_BUTTON.addActionListener(l -> {
            this.controller.disconnect();
            this.clearConnectionPanel();
        });

        // Message panel elements defined
        this.addLabel(new JLabel("Sender"), true, 30, 243, 60, 22);
        this.addLabel(new JLabel("Recipient"), true, 232, 243, 60, 22);
        this.addLabel(new JLabel("Topic"), true, 30, 275, 60, 22);
        this.addLabel(new JLabel("Subject"), true, 232, 275, 60, 22);
        this.addLabel(new JLabel("Contents"), true, 30, 313, 60, 22);

        this.addTextField(SENDER_FIELD, true, 102, 243, 100, 22);
        this.addTextField(RECIPIENT_FIELD, true, 304, 243, 100, 22);
        this.addTextField(TOPIC_FIELD, true, 102, 275, 100, 22);
        this.addTextField(SUBJECT_FIELD, true, 304, 275, 100, 22);
        this.addTextArea(CONTENTS_AREA, 1, 102, 313, 302, 176);

        this.addButton(CLEAR_CONTENTS_BUTTON, true, 102, 505, 100, 30);
        this.addButton(STORED_MESSAGES_BUTTON, true, 219, 505, 100, 30);
        this.addButton(CREATE_MESSAGE_BUTTON, true, 336, 505, 100, 30);

        CLEAR_CONTENTS_BUTTON.addActionListener(l -> this.clearMessagePanel());

        STORED_MESSAGES_BUTTON.addActionListener(l -> {
            this.setVisible(false);
            new MessageView(this);
        });

        CREATE_MESSAGE_BUTTON.addActionListener(l -> {
            // Required message fields validated before creation
            if (this.requiredMessageInfoSupplied()) {
                // Optional info collection
                final String recipient = RECIPIENT_FIELD.getText().strip(),
                                 topic = TOPIC_FIELD.getText().strip(),
                               subject = SUBJECT_FIELD.getText().strip();
                // Message object created and stored locally
                MessageController.storeMessage(new Message(
                    SENDER_FIELD.getText().strip(),
                    recipient.isEmpty() ? null : recipient,
                    topic.isEmpty() ? null : topic,
                    subject.isEmpty() ? null : subject,
                    CONTENTS_AREA.getText().split("\\r?\\n")
                ));
                this.showMessageDialog("Message created and stored successfully", 1);
                this.clearMessagePanel();
            } else
                this.showMessageDialog("Please specify the message sender and/or contents", 2);
        });

        // Activity panel elements defined
        this.addTextArea(ACTIVITY_AREA, 2, 460, 37, 742, 418);
        this.addLabel(new JLabel("Request"), true, 504, 467, 65, 22);
        this.addTextField(REQUEST_FIELD, false, 590, 467, 538, 22);
        this.addButton(CLEAR_REQUEST_BUTTON, false, 983, 505, 100, 30);
        this.addButton(SEND_REQUEST_BUTTON, false, 1100, 505, 100, 30);

        CLEAR_REQUEST_BUTTON.addActionListener(l -> {
            REQUEST_FIELD.setText(null);
            REQUEST_FIELD.requestFocusInWindow();
        });

        SEND_REQUEST_BUTTON.addActionListener(l -> {
            final String input = REQUEST_FIELD.getText().strip();
            if (!input.isEmpty()) {
                this.controller.handleRequest(input);
                REQUEST_FIELD.setText(null);
            } else
                this.showMessageDialog("Please supply a valid request", 2);
            REQUEST_FIELD.requestFocusInWindow();
        });

        // Live datetime label initialised alongside the connection, message and activity panels
        // Calling `setVisible` displays the frame once the constructor is invoked
        this.handleDateTimeLabel(10, 4);
        this.panel.add(this.createPanel(1, 0, 29, 450, 150));
        this.panel.add(this.createPanel(2, 0, 207, 450, 316));
        this.panel.add(this.createPanel(3, 450, 7, 761, 516));
        this.setVisible(true);
    }

    private boolean connectionInfoSupplied() {
        return !IDENTIFIER_FIELD.getText().isBlank()
            && !IP_ADDRESS_FIELD.getText().isBlank()
            && !PORT_NUM_FIELD.getText().isBlank();
    }

    private boolean connectionInfoIsValid(String host, String port) {
        return (host.equals("localhost")
            || Pattern.compile(IPv4_ADDRESS_REGEX).matcher(host).matches())
            && Pattern.compile(PORT_NUM_REGEX).matcher(port).matches();
    }

    private void clearConnectionPanel() {
        IDENTIFIER_FIELD.setText(null);
        IP_ADDRESS_FIELD.setText(null);
        PORT_NUM_FIELD.setText(null);
        IDENTIFIER_FIELD.requestFocusInWindow();
    }

    private void clearMessagePanel() {
        SENDER_FIELD.setText(null);
        RECIPIENT_FIELD.setText(null);
        TOPIC_FIELD.setText(null);
        SUBJECT_FIELD.setText(null);
        CONTENTS_AREA.setText(null);
        SENDER_FIELD.requestFocusInWindow();
    }

    private boolean requiredMessageInfoSupplied() {
        return !SENDER_FIELD.getText().isBlank()
            && !CONTENTS_AREA.getText().isBlank();
    }

    public ClientController getController() {
        return this.controller;
    }

    public void showMessageDialog(String message, int type) {
        JOptionPane.showInternalMessageDialog(
            this.panel,
            message,
            null,
            type == 1 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
        );
    }

    public void updateActivityArea(String text, String peer) {
        // The text to be displayed in the activity area is split by line to account for multi-line
        // outputs and indicates whether it is on behalf of the client or connected peer(s)
        final String[] meta = text.split("\n");
        if (peer != null) {
            ACTIVITY_AREA.append(
                text.startsWith("ACK? PM/") ? String.format(
                    "[%s] PEER: %s\n",
                    ACTIVITY_DATETIME_FORMAT.format(new Date()),
                    meta[0]
                ) : String.format(
                    "[%s] PEER: (%s) %s\n",
                    ACTIVITY_DATETIME_FORMAT.format(new Date()),
                    peer,
                    meta[0]
                )
            );
        } else {
            ACTIVITY_AREA.append(
                String.format(
                    "[%s]   ME: %s\n",
                    ACTIVITY_DATETIME_FORMAT.format(new Date()),
                    meta[0]
                )
            );
        }

        final int lines = meta.length;
        if (lines > 1) {
            for (int i = 1; i < lines; i++)
                ACTIVITY_AREA.append(String.format("\t\t\t    | %s\n", meta[i]));
        }
    }

    public void setConnectionPanelState(boolean enable, boolean peer) {
        IDENTIFIER_FIELD.setEditable(enable);
        IP_ADDRESS_FIELD.setEditable(enable);
        PORT_NUM_FIELD.setEditable(enable);
        CONNECT_BUTTON.setEnabled(enable);
        CLEAR_INFO_BUTTON.setEnabled(enable);
        if (!peer)
            DISCONNECT_BUTTON.setEnabled(!enable);
    }

    public void setActivityPanelState(boolean enable) {
        REQUEST_FIELD.setEditable(enable);
        CLEAR_REQUEST_BUTTON.setEnabled(enable);
        SEND_REQUEST_BUTTON.setEnabled(enable);
        if (enable)
            REQUEST_FIELD.requestFocusInWindow();
    }
}
