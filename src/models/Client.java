
package models;

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
        String.format("ACK? PM/%d.0 ", MIN_PROTOCOL_VERSION);

    private Socket socket;      // Client's connection socket

    public Client(Socket socket) {
        this.socket = socket;
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
            writer.println(PROTOCOL_ACK_MESSAGE + this.socket.getLocalSocketAddress());
            String request = reader.readLine();
            final String[] meta = request.split("\\s+");
            if (meta.length == 3 && meta[0].equals("ACK?")) {
                final int protocol = (int) Double.parseDouble(meta[1].split("/")[1]);
                if (protocol >= MIN_PROTOCOL_VERSION)
                    writer.printf("Hi %s! (%s)\n", meta[2], this.socket.getRemoteSocketAddress());
            }

            // Streams are closed promptly once communication ends
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Client socket is closed last
            try {
                this.socket.close();
                System.out.printf(
                    "Client at %s disconnected\n",
                    this.socket.getRemoteSocketAddress()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
