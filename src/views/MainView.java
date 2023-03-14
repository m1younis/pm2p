
package views;

import javax.swing.JButton;
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

    public MainView() {
        super("pm2p", 0, 0);
    }
}