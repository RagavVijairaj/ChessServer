import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Viewer {
    BufferedReader in;
    PrintWriter out;
    Socket socket;
    final int viewerGameId;
    static final ConcurrentHashMap<Integer, Viewer> allViewers = new ConcurrentHashMap<>();
    boolean isGameUpdated = false;
    private final Object lock = new Object();

    public Viewer(Socket socket, int gameId) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.socket = socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.viewerGameId = gameId;
        Streamer.addViewer(this);
        keepConectionAlive();
    }


    public void sendMessage(String message) {
        out.println(message);
    }

    //to keep the connection alive
    public void keepConectionAlive(){
        while (true){
            Scanner scanner = new Scanner(System.in);
            if(scanner.nextLine().equals("")){
                out.println("");
            }
        }
    }

    public void close() {
        if (socket == null) return; // Prevent double cleanup
        System.out.println("Closing viewer for game ID: " + viewerGameId);
        try {
            allViewers.remove(viewerGameId);
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (Exception e) {
            System.err.println("Error closing viewer: " + e.getMessage());
        } finally {
            socket = null;
            in = null;
            out = null;
        }
    }
}





