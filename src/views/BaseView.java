
package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

/**
 * A class for setting the application's user interface foundations.
 *
 * <p>
 * The methods defined here perform specific tasks for managing the app's UI, and are used to
 * populate its various elements at runtime - the dimensions, positioning and styling of a given
 * component are simultaneously considered in each, reducing code complexity in derived classes.
 * </p>
 *
 * <p>
 * All have been declared with <code>protected</code> visibility, meaning they can only be visible
 * in and invoked by subclasses.
 * </p>
 */
public abstract class BaseView extends JFrame {
    // App and button background colours declared along with UI fonts
    private static final Color BASE_COLOUR = new Color(240, 240, 240),
                             BUTTON_COLOUR = new Color(228, 228, 228);
    private static final Font BORDER_FONT = new Font("Franklin Gothic Medium", Font.PLAIN, 18),
                               LABEL_FONT = new Font("Lucida Sans", Font.BOLD, 12),
                               COMMS_FONT = new Font("Lucida Sans", Font.PLAIN, 11);

    // Borders defined to separate each in-app panel
    private static final Border
        BASE_BORDER = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
        CONNECTION_BORDER = BorderFactory.createTitledBorder(
            BASE_BORDER,
            "Connection Details",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BORDER_FONT,
            Color.BLACK
        ),
        MESSAGE_BORDER = BorderFactory.createTitledBorder(
            BASE_BORDER,
            "Message Details",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BORDER_FONT,
            Color.BLACK
        ),
        ACTIVITY_BORDER = BorderFactory.createTitledBorder(
            BASE_BORDER,
            "Activity",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BORDER_FONT,
            Color.BLACK
        );

    // UI datetime label formatting set
    private static final SimpleDateFormat
        DATETIME_LABEL_FORMAT = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");

    // Main panel housing all UI elements
    protected JPanel panel;

    public BaseView(String title, int width, int height) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setBounds(0, 0, width, height);
        this.setLocationRelativeTo(null);

        this.panel = new JPanel(null);
        this.panel.setBackground(BASE_COLOUR);
        this.panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.panel);
    }

    protected void addLabel(JLabel component, boolean label, int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
        component.setForeground(Color.BLACK);

        if (label) {
            component.setHorizontalAlignment(JLabel.RIGHT);
            component.setFont(LABEL_FONT);
        } else
            component.setFont(COMMS_FONT);

        this.panel.add(component);
    }

    protected void addTextArea(JTextArea component, int x, int y, int width, int height) {
        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(),
            BorderFactory.createEmptyBorder(3, 2, 3, 2)
        ));
        component.setForeground(Color.BLACK);
        component.setFont(COMMS_FONT);
        component.setEditable(false);            // Text areas will be disabled on app startup

        // Automatic scrolling configured
        final JScrollPane pane = new JScrollPane(component);
        pane.setBounds(x, y, width, height);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ((DefaultCaret) component.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.panel.add(pane);
    }

    protected void addTextField(
        JTextField component,
        boolean enable,
        int x,
        int y,
        int width,
        int height) {
        component.setBounds(x, y, width, height);
        component.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(0, 2, 0, 2)
        ));
        component.setForeground(Color.BLACK);
        component.setFont(COMMS_FONT);
        if (!enable)
            component.setEditable(false);

        this.panel.add(component);
    }

    protected void addButton(
        JButton component,
        boolean enable,
        int x,
        int y,
        int width,
        int height) {
        component.setBounds(x, y, width, height);
        component.setBackground(BUTTON_COLOUR);
        component.setForeground(Color.BLACK);
        component.setFocusPainted(false);
        component.setFont(LABEL_FONT.deriveFont(11.0f));
        if (!enable)
            component.setEnabled(false);

        // Hover effect added by way of the `MouseEvent` and `MouseListener` classes
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {
                if (component.isEnabled())
                    component.setBackground(BUTTON_COLOUR);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (component.isEnabled())
                    component.setBackground(new Color(220, 237, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (component.isEnabled())
                    component.setBackground(BUTTON_COLOUR);
            }
        });

        this.panel.add(component);
    }

    protected JPanel createPanel(int type, int x, int y, int width, int height) {
        // 1 => connection panel, 2 => message panel, 3 => activity panel
        final JPanel component = new JPanel();
        component.setBounds(x, y, width, height);
        component.setBackground(BASE_COLOUR);

        if (type == 1)
            component.setBorder(CONNECTION_BORDER);
        else if (type == 2)
            component.setBorder(MESSAGE_BORDER);
        else if (type == 3)
            component.setBorder(ACTIVITY_BORDER);

        return component;
    }

    protected void handleDateTimeLabel(int x, int y) {
        final JLabel component = new JLabel();
        this.addLabel(component, false, x, y, 200, 24);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                component.setText(DATETIME_LABEL_FORMAT.format(new Date()));
            }
        }, 0, 1000);
    }
}
