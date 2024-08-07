
package dev.m1younis.view;

import dev.m1younis.model.Message;
import dev.m1younis.controller.MessageController;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * A class defining the stored messages view.
 */
public class MessageView extends BaseView {
    private static final String[] MESSAGES_TABLE_HEADERS =
        new String[]{"Message-uid (SHA-256)", "Created", "Sender"};

    private static final JButton CLOSE_BUTTON = new JButton("Close");

    private MainView main;

    private Map<String, Message> messages;

    public MessageView(MainView main) {
        super("pm2p: Messages", 850, 360);

        this.main = main;
        this.messages = MessageController.loadStoredMessages();

        this.addButton(CLOSE_BUTTON, true, 724, 281, 100, 30);

        CLOSE_BUTTON.addActionListener(l -> {
            this.main.setVisible(true);
            this.dispose();
        });

        this.addLabel(
            new JLabel("Select message then ENTER to view details"),
            true,
            0,
            28,
            280,
            20
        );

        // `JTable` created with a custom model and properties
        final DefaultTableModel tableModel = new DefaultTableModel(MESSAGES_TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final JTable table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getColumnModel().getColumn(0).setMinWidth(375);
        table.getColumnModel().getColumn(1).setMinWidth(85);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ENTER key bind cleared; see L103
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none"
        );

        // Rows populated based on stored message records
        this.messages.forEach((uid, message) ->
            tableModel.addRow(new Object[]{
                uid, this.formatMessageTimestamp(message.getCreated()), message.getSender()
            })
        );

        // `MouseListener` for handling row selection and deselection
        table.addMouseListener(new MouseAdapter() {
            private int lastSelected = -1;
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row == lastSelected) {
                    table.clearSelection();
                    lastSelected = -1;
                } else
                    lastSelected = row;
            }
        });

        // `KeyListener` set for ENTER to display details of a selected message
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = table.getSelectedRow();
                    if (row != -1)
                        showMessageDetails(messages.get(table.getValueAt(row, 0)));
                }
            }
        });

        final JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBounds(30, 55, 775, 215);
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        this.panel.add(wrapper);

        this.handleDateTimeLabel(10, 295);
        this.panel.add(this.createPanel(4, 0, 6, 835, 292));
        this.setVisible(true);
    }

    private String formatMessageTimestamp(long epoch) {
        return Instant.ofEpochSecond(epoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss"));
    }

    private void showMessageDetails(Message message) {
        final String recipient =
            Objects.requireNonNullElse(message.getRecipient(), "Not specified"),
                         topic = Objects.requireNonNullElse(message.getTopic(), "Not specified"),
                       subject = Objects.requireNonNullElse(message.getSubject(), "Not specified");

        final StringJoiner sj = new StringJoiner("\n")
            .add(String.format("Sender: %s", message.getSender()))
            .add(String.format("Recipient: %s", recipient))
            .add(String.format("Topic: %s", topic))
            .add(String.format("Subject: %s", subject));

        final String[] contents = message.getContents();
        sj.add(String.format("Contents: %d", contents.length))
            .add(String.join("\n", contents));

        JOptionPane.showInternalMessageDialog(
            null,
            sj.toString(),
            "Message Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
