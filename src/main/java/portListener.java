import java.io.*;
import java.net.*;

public class PortListener {
    ServerSocket serverSocket;


    public void startPortAndListen(int portNum) {
        try {
            serverSocket = new ServerSocket(portNum);
            System.out.println("Server started on port " + portNum);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (Exception e) {}
    }

    private void handleClient(Socket socket) {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String message = in.readLine();
            if (message == null) {
                System.out.println("Client disconnected during initial message.");
                return;
            }

            System.out.println("Received message: " + message);
            String[] infoList = message.split(",");
            String init = infoList[0];
            String code = infoList[1];
            switch (init) {
                case "init: streamer":
                    System.out.println("Streamer connected");
                    new Streamer(socket, Integer.parseInt(code));
                    break;
                case "init: viewer":
                    System.out.println("Viewer connected");
                    new Viewer(socket, Integer.parseInt(code));
                    break;
                default:
                    System.out.println("Unknown message: " + message);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }


}
