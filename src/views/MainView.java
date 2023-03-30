
package views;

import controllers.ClientController;
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
                                 VIEW_SAVED_BUTTON = new JButton("View Saved"),
                               SEND_MESSAGE_BUTTON = new JButton("Send"),
                              CLEAR_REQUEST_BUTTON = new JButton("Clear"),
                               SEND_REQUEST_BUTTON = new JButton("Send");

    // Regex for validating connection info defined
    private static final String IPv4_ADDRESS_REGEX =
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
                                    PORT_NUM_REGEX = "[0-9]{1,5}";
    
    public MainView() {
        super("pm2p", 1226, 586);

        new ClientController(this);      // Client handler startup

        // Connection panel elements defined
        this.addLabel(new JLabel("Identifier"), true, 74, 66, 80, 22);
        this.addLabel(new JLabel("IP Address"), true, 74, 94, 80, 22);
        this.addLabel(new JLabel("Port"), true, 74, 122, 80, 22);

        this.addTextField(IDENTIFIER_FIELD, true, 166, 66, 150, 22);
        this.addTextField(IP_ADDRESS_FIELD, true, 166, 94, 150, 22);
        this.addTextField(PORT_NUM_FIELD, true, 166, 122, 150, 22);

        this.addButton(CLEAR_INFO_BUTTON, true, 102, 164, 100, 30);
        this.addButton(CONNECT_BUTTON, true, 219, 164, 100, 30);
        this.addButton(DISCONNECT_BUTTON, false, 336, 164, 100, 30);

        CONNECT_BUTTON.addActionListener(e -> {
            // Connection details validated once the "Connect" button is submitted
            if (!this.connectionFieldsAreEmpty()) {
                final String host = IP_ADDRESS_FIELD.getText(),
                            iport = PORT_NUM_FIELD.getText();
                if (this.connectionInfoIsValid(host, iport)) {
                    // Supplied info is recorded after validation and used in establishing the
                    // connection to a peer
                    final int port = Integer.parseInt(iport);
                    this.displayMessage(
                        port >= 1 && port <= 65535 ? "Still a bunch to do!" :
                        "Port number must be between 1 and 65535 inclusive"
                    );
                } else
                    this.displayMessage("Invalid IP address and/or port number submitted");
            } else
                this.displayMessage("Please fill in all/missing connection information");
        });

        CLEAR_INFO_BUTTON.addActionListener(e -> {
            IDENTIFIER_FIELD.setText(null);
            IP_ADDRESS_FIELD.setText(null);
            PORT_NUM_FIELD.setText(null);
            IDENTIFIER_FIELD.requestFocusInWindow();
        });

        // Message panel elements defined
        this.addLabel(new JLabel("Sender"), true, 30, 246, 60, 22);
        this.addLabel(new JLabel("Recipient"), true, 232, 246, 60, 22);
        this.addLabel(new JLabel("Topic"), true, 30, 278, 60, 22);
        this.addLabel(new JLabel("Subject"), true, 232, 278, 60, 22);
        this.addLabel(new JLabel("Contents"), true, 30, 316, 60, 22);

        this.addTextField(SENDER_FIELD, false, 102, 246, 100, 22);
        this.addTextField(RECIPIENT_FIELD, false, 304, 246, 100, 22);
        this.addTextField(TOPIC_FIELD, false, 102, 278, 100, 22);
        this.addTextField(SUBJECT_FIELD, false, 304, 278, 100, 22);
        this.addTextArea(CONTENTS_AREA, 1, 102, 316, 302, 176);

        this.addButton(CLEAR_CONTENTS_BUTTON, false, 102, 508, 100, 30);
        this.addButton(VIEW_SAVED_BUTTON, false, 219, 508, 100, 30);
        this.addButton(SEND_MESSAGE_BUTTON, false, 336, 508, 100, 30);

        // Activity panel elements defined
        this.addTextArea(ACTIVITY_AREA, 2, 460, 40, 742, 418);
        this.addLabel(new JLabel("Request"), true, 504, 470, 65, 22);
        this.addTextField(REQUEST_FIELD, false, 590, 470, 538, 22);
        this.addButton(CLEAR_REQUEST_BUTTON, false, 983, 508, 100, 30);
        this.addButton(SEND_REQUEST_BUTTON, false, 1100, 508, 100, 30);

        // Live datetime label initialised alongside the connection, message and activity panels
        // Calling `setVisible` displays the frame once the constructor is invoked
        this.handleDateTimeLabel(10, 6);
        this.panel.add(this.createPanel(1, 0, 32, 450, 150));
        this.panel.add(this.createPanel(2, 0, 210, 450, 316));
        this.panel.add(this.createPanel(3, 450, 10, 761, 516));
        this.setVisible(true);
    }

    private boolean connectionFieldsAreEmpty() {
        return IDENTIFIER_FIELD.getText().isEmpty()
            && IP_ADDRESS_FIELD.getText().isEmpty()
            && PORT_NUM_FIELD.getText().isEmpty();
    }

    private boolean connectionInfoIsValid(String host, String port) {
        return (host.equals("localhost")
            || Pattern.compile(IPv4_ADDRESS_REGEX).matcher(host).matches())
            && Pattern.compile(PORT_NUM_REGEX).matcher(port).matches();
    }

    private void displayMessage(String message) {
        JOptionPane.showInternalMessageDialog(this.panel, message);
    }

    public void updateActivityArea(String message) {
        ACTIVITY_AREA.append(message + "\n");
    }
}
