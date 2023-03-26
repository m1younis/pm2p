
package models;

import views.MainView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A class representing a connected client thread and the PM protocol it communicates with.
 * 
 */
public class Client extends Thread {
    // PM communication protocol constants
    private static final int MIN_PROTOCOL_VERSION = 1;
    private static final String PROTOCOL_ACK_MESSAGE =
        String.format("ACK? PM/%d ", MIN_PROTOCOL_VERSION);

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
            this.ui.updateActivityArea(message);

            String request = reader.readLine();
            final String[] meta = request.split("\\s+");
            if (meta.length == 3 && meta[0].equals("ACK?")) {
                final int protocol = Integer.parseInt(meta[1].split("/")[1]);
                this.identifier = meta[2];
                if (protocol >= MIN_PROTOCOL_VERSION) {
                    acknowledged = true;
                    message = String.format("%s (%s) joined", this.identifier, this.address);
                    writer.println(message);
                    this.ui.updateActivityArea(String.join("\n", request, message));
                }
            }

            // Requests and responses can now be handled as the protocol is acknowledged between
            // the client and peer
            while (acknowledged) {
                request = reader.readLine();
                String response = null;
                if (request.equals("QUIT!"))
                    acknowledged = false;
                else if (request.equals("TIME?"))
                    response = String.format("NOW %d", System.currentTimeMillis() / 1000);
                else
                    break;

                if (response != null) {
                    writer.println(response);
                    this.ui.updateActivityArea(String.join("\n", request, response));
                } else
                    this.ui.updateActivityArea(request);
            }

            // Streams are closed promptly once communication ends or the protocol is broken due to
            // an invalid request
            if (this.identifier != null) {
                if (acknowledged) {
                    this.ui.updateActivityArea(request);
                    message = String.format("%s (%s) was kicked", this.identifier, this.address);
                } else
                    message = String.format("%s (%s) left", this.identifier, this.address);
                writer.println(message);
                this.ui.updateActivityArea(message);
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
