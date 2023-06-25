
package dev.m1younis.controller;

import dev.m1younis.model.Client;
import dev.m1younis.view.MainView;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A class acting as the app's server handler for incoming client threads (peers) and client
 * controller for outgoing connections.
 */
public class ClientController extends Thread {
    private static final int DEFAULT_PORT = 1123;        // Default server port

    // Server socket's connection fields
    private ServerSocket server;
    private Socket socket;

    private MainView ui;               // Allows the handler and UI to interact

    private List<Client> peers;                       // Tracks connected peers

    public ClientController(MainView ui) {
        this.ui = ui;
        this.peers = new ArrayList<>();
        try {
            this.server = new ServerSocket(DEFAULT_PORT);
            this.start();          // Invokes the `run` method's implementation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePeer(Client client) {
        this.peers.remove(client);
        // Reactivates connection panel once all peers have left
        if (this.peers.isEmpty())
            this.ui.setConnectionPanelState(true, true);
    }

    @Override
    public void run() {
        System.out.printf("Server started at %d...\n", DEFAULT_PORT);
        try {
            while (true) {
                // The `accept` method below listens for incoming clients on the server port
                // specified above
                this.socket = this.server.accept();
                // Prevents UI from establishing an outgoing connection given incoming connections
                if (this.peers.isEmpty())
                    this.ui.setConnectionPanelState(false, true);
                final Client client = new Client(this.socket, this.ui, null, true);
                this.peers.add(client);
                client.start();
                System.out.printf(
                    "Client connected from %s\n",
                    this.socket.getRemoteSocketAddress()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
