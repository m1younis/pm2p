
package controllers;

import models.Client;
import views.MainView;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A class acting as the app's server handler for incoming client threads.
 */
public class ClientController extends Thread {
    private static final int DEFAULT_PORT = 1123;        // Default server port

    // Server socket's connection fields
    private ServerSocket server;
    private Socket socket;

    private MainView ui;               // Allows the handler and UI to interact

    public ClientController(MainView ui) {
        this.ui = ui;
        try {
            this.server = new ServerSocket(DEFAULT_PORT);
            this.start();      // Invokes the `run` method's implementation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.printf("Server started at %d...\n", DEFAULT_PORT);
        try {
            while (true) {
                // The `accept` method below listens for incoming clients on the server port
                // specified above
                this.socket = this.server.accept();
                new Client(this.socket, this.ui).start();
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
