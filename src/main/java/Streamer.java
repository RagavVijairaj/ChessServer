import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

class Streamer{

      BufferedReader in;
      PrintWriter out;
      Socket socket;
      final int streamerGameId;

      static  ConcurrentHashMap<Integer, Streamer> allStreamers = new ConcurrentHashMap<>();
      static  ConcurrentHashMap<Streamer, ArrayList<Viewer>> allViewerMap = new ConcurrentHashMap<>();

    public Streamer(Socket socket, int gameId) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.socket = socket;
        }catch (IOException e){}

        this.streamerGameId = gameId;

        allStreamers.put(streamerGameId, this);
        allViewerMap.put(this, new ArrayList<>());

        listenForCommands();


    }


    public static int addViewer(Viewer viewer) {
        System.out.println("adding");
        Streamer streamer = allStreamers.get(viewer.viewerGameId);
        if (streamer == null) {
            System.out.println("closing");
            viewer.close();
            return -1;
        }

        allViewerMap.get(streamer).add(viewer);
        return 0;
    }

    private void listenForCommands(){


        try {
            String message;
            while ((message = in.readLine()) != null) {
                message = message.trim();
                switch (message) {
                    case "stop":
                        System.out.println("Stop command received");
                        close();
                        return;

                    case "game updated":
                        System.out.println("Game updated");
                        notifyViewers();
                        break;

                }
            }

        }catch (Exception e){
            close();
        }



    }

     public void notifyViewers() {
        ArrayList<Viewer> viewers = allViewerMap.get(this);
        if (viewers != null) {
            viewers.forEach(viewer -> viewer.sendMessage("game updated"));
        }
    }


    public void close() {
        System.out.println("closing streamer");
        try {
            allStreamers.remove(streamerGameId);
            allViewerMap.remove(this);

            ArrayList<Viewer> viewers = allViewerMap.get(this);
            if (viewers != null) {
                viewers.forEach(viewer -> viewer.close());
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                socket = null;
                in = null;
                out = null;
            }

            System.out.println("Streamer closed: " + streamerGameId);
        } catch (Exception e) {
            System.err.println("Error closing streamer: " + e.getMessage());
        }
    }
}
