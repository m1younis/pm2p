
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
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * A class representing a connected client thread and the PM protocol it communicates with.
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
        "\tRetrieves a stored message object from the peer by its unique identifier",
        "\t`hash`, which is equivalent to the message body's SHA-256 sum",
        "SHOW? <since> <headers>",
        "\tLists the SHA-256 sum of all message objects created on or after `since`",
        "\tand contain the contents specified by `headers`",
        "QUIT!\tEnds the communication between two peers politely"
    };

    // Regex defined to detect `SHOW?` requests with a non-zero amount of headers
    private static final String SHOW_REQUEST_HEADERS_REGEX =
        "^SHOW\\?\\s(0|[1-9]\\d*)\\s[1-9]\\d*$";

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

        return entries.isEmpty() ? "NONE" : new StringJoiner("\n")
            .add(String.format("ENTRIES %d", entries.size()))
            .add(String.join("\n", entries))
            .toString();
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
                            response = new StringJoiner("\n")
                                .add("SUCCESS")
                                .add(target.toString())
                                .toString();
                        } else
                            response = "NOT FOUND";
                    } else
                        break;
                } else if (request.startsWith("SHOW?")) {
                    meta = request.split("\\s+");
                    if (meta.length == 3) {
                        final long since = Long.parseLong(meta[1]);
                        final int contents = Integer.parseInt(meta[2]);
                        // `since` => non-negative + non-future, `headers` => non-negative
                        if (since < System.currentTimeMillis() / 1000
                            && since >= 0
                            && contents >= 0) {
                            if (contents != 0) {
                                this.ui.updateActivityArea(request, false);
                                // The number of lines in the content to search for in the message
                                // <==> `contents` and is compiled by a `StringJoiner` object
                                final StringJoiner sj = new StringJoiner("\n");
                                for (int i = 0; i < contents; i++) {
                                    final String line = reader.readLine();
                                    sj.add(line);
                                    this.ui.updateActivityArea(line, false);
                                }
                                response = this.showRequestHandler(since, sj.toString());
                            } else
                                response = this.showRequestHandler(since, null);
                        } else
                            break;
                    } else
                        break;
                } else
                    break;

                if (response != null) {
                    writer.println(response);
                    // All but non-zero header `SHOW?` requests are displayed in the activity log
                    // since this is handled within the main loop
                    if (!Pattern.compile(SHOW_REQUEST_HEADERS_REGEX).matcher(request).matches())
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
