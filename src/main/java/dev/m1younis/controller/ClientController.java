
package dev.m1younis.controller;

import dev.m1younis.model.Client;
import dev.m1younis.view.MainView;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A class acting as the app's server handler for incoming client threads.
 */
public class ClientController extends Thread {
    private static final int DEFAULT_PORT = 1123;        // Default server port

    // Server socket's connection fields
    private ServerSocket server;
    private Socket socket;

    private MainView ui;               // Allows the handler and UI to interact

    private List<Client> clients;                   // Tracks connected clients

    public ClientController(MainView ui) {
        this.ui = ui;
        this.clients = new ArrayList<>();
        try {
            this.server = new ServerSocket(DEFAULT_PORT);
            this.start();          // Invokes the `run` method's implementation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(Client thread) {
        this.clients.remove(thread);
        if (this.clients.isEmpty())      // Reactivates connection panel once all clients have left
            this.ui.setConnectionPanelState(true);
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
                if (this.clients.isEmpty())
                    this.ui.setConnectionPanelState(false);
                final Client client = new Client(this.socket, this.ui);
                this.clients.add(client);
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
