
package models;

import controllers.MessageController;
import views.MainView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a connected client thread and the PM protocol it communicates with.
 * 
 */
public class Client extends Thread {
    // PM communication protocol constants
    private static final int MIN_PROTOCOL_VERSION = 1;
    private static final String PROTOCOL_ACK_MESSAGE =
        String.format("ACK? PM/%d ", MIN_PROTOCOL_VERSION);
    private static final String[] PROTOCOL_HELP_MESSAGE = {
        String.format("Requests supported in PM (v%d)", MIN_PROTOCOL_VERSION),
        "HELP?\tDisplays this message",
        "TIME?\tReturns the current time (in Unix Epoch) at the receiving peer",
        "LOAD? <hash>",
        "\tRetrieves a stored messages object from the peer by its unique identifier",
        "\t<hash> which is equivalent to the message body's SHA-256 sum",
        "QUIT!\tEnds the communication between two peers politely"
    };

    // Client's connection socket fields
    private Socket socket;
    private String address,
                identifier;

    private MainView ui;        // Allows the client thread and UI to interact

    public Client(Socket socket, MainView ui) {
        this.socket = socket;
        this.address = socket.getRemoteSocketAddress().toString();
        this.ui = ui;
    }

    private String showRequestHandler(long since, String content) {
        List<String> entries = new ArrayList<>();
        MessageController.loadStoredMessages()
            .values()
            .forEach(message -> {
                if (message.getCreated() >= since) {
                    final String hash = message.getHash();
                    if (content != null) {
                        if (message.toString().contains(content))
                            entries.add(hash);
                    } else
                        entries.add(hash);
                }
            });

        return entries.isEmpty() ? "NONE" : String.format("ENTRIES %d", entries.size());
    }

    @Override
    public void run() {
        try {
            // Client communication streams initialised
            final BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            final PrintWriter writer =
                new PrintWriter(this.socket.getOutputStream(), true);

            // Protocol acknowledgement MUST occur prior to any communication
            String message = PROTOCOL_ACK_MESSAGE + this.socket.getLocalAddress();
            boolean acknowledged = false;
            writer.println(message);
            this.ui.updateActivityArea(message, true);

            String request = reader.readLine();
            String[] meta = request.split("\\s+");
            if (meta.length == 3 && request.startsWith("ACK? PM/")) {
                final int protocol = Integer.parseInt(meta[1].split("/")[1]);
                this.identifier = meta[2];
                if (protocol >= MIN_PROTOCOL_VERSION) {
                    acknowledged = true;
                    message = String.format("%s (%s) joined", this.identifier, this.address);
                    writer.println(message);
                    this.ui.updateActivityArea(request, false);
                    this.ui.updateActivityArea(message, true);
                }
            }

            // Requests and responses can now be handled as the protocol is acknowledged between
            // the client and peer
            while (acknowledged) {
                request = reader.readLine();
                String response = null;
                if (request.equals("QUIT!"))
                    acknowledged = false;
                else if (request.equals("HELP?"))
                    response = String.join("\n", PROTOCOL_HELP_MESSAGE);
                else if (request.equals("TIME?"))
                    response = String.format("NOW %d", System.currentTimeMillis() / 1000);
                else if (request.startsWith("LOAD?")) {
                    meta = request.split("\\s+");
                    if (meta.length == 2) {
                        final Message target =
                            MessageController.loadStoredMessages().getOrDefault(meta[1], null);
                        if (target != null) {
                            message = target.toString();
                            response = String.join(
                                "\n",
                                "SUCCESS",
                                message.substring(0, message.length() - 1)
                            );
                        } else
                            response = "NOT FOUND";
                    } else
                        break;
                } else
                    break;

                if (response != null) {
                    writer.println(response);
                    this.ui.updateActivityArea(request, false);
                    this.ui.updateActivityArea(response, true);
                } else
                    this.ui.updateActivityArea(request, false);
            }

            // Streams are closed promptly once communication ends or the protocol is broken due to
            // an invalid request
            if (this.identifier != null) {
                if (acknowledged) {
                    this.ui.updateActivityArea(request, false);
                    message = String.format("%s (%s) was kicked", this.identifier, this.address);
                } else
                    message = String.format("%s (%s) left", this.identifier, this.address);
                writer.println(message);
                this.ui.updateActivityArea(message, true);
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Client connection socket is closed last
            try {
                this.socket.close();
                System.out.printf("Client at %s disconnected\n", this.address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
