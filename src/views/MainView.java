
package views;

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
                               SEND_REQUEST_BUTTON = new JButton("Send");

    // Regex for validating connection info defined
    private static final String IPv4_ADDRESS_REGEX =
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
                                    PORT_NUM_REGEX = "[0-9]{1,5}";

    public MainView() {
        super("pm2p", 465, 250);

        // Connection info panel elements defined
        this.addLabel(new JLabel("Identifier"), true, 82, 66, 80, 22);
        this.addLabel(new JLabel("IP Address"), true, 82, 94, 80, 22);
        this.addLabel(new JLabel("Port"), true, 82, 122, 80, 22);

        this.addTextField(IDENTIFIER_FIELD, true, 174, 66, 150, 22);
        this.addTextField(IP_ADDRESS_FIELD, true, 174, 94, 150, 22);
        this.addTextField(PORT_NUM_FIELD, true, 174, 122, 150, 22);

        this.addButton(CLEAR_INFO_BUTTON, true, 104, 164, 100, 30);
        this.addButton(CONNECT_BUTTON, true, 218, 164, 100, 30);
        this.addButton(DISCONNECT_BUTTON, false, 332, 164, 100, 30);

        CONNECT_BUTTON.addActionListener(e -> {
            // Connection details validated once the "Connect" button is submitted
            if (!this.connectionFieldsAreEmpty()) {
                if (this.connectionInfoIsValid()) {
                    // Supplied info is recorded after validation and used in establishing the
                    // connection to a peer
                    final int port = Integer.parseInt(PORT_NUM_FIELD.getText());
                    this.displayMessage(
                        port >= 1 && port <= 65353 ? "Still a bunch to do!" :
                        "Port number must be between 1 and 65353 inclusive"
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

        // Live datetime label and connection info panel initialised
        this.handleDateTimeLabel(10, 6);
        this.panel.add(this.createPanel(1, 0, 32, 450, 150));

        this.setVisible(true);
    }

    private boolean connectionFieldsAreEmpty() {
        return IDENTIFIER_FIELD.getText().isEmpty()
            && IP_ADDRESS_FIELD.getText().isEmpty()
            && PORT_NUM_FIELD.getText().isEmpty();
    }

    private boolean connectionInfoIsValid() {
        return Pattern.compile(IPv4_ADDRESS_REGEX).matcher(IP_ADDRESS_FIELD.getText()).matches()
            && Pattern.compile(PORT_NUM_REGEX).matcher(PORT_NUM_FIELD.getText()).matches();
    }

    private void displayMessage(String message) {
        JOptionPane.showInternalMessageDialog(this.panel, message);
    }

    public static void main(String[] args) {
        new MainView();
    }
}
