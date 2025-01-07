import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PortListener listener = new PortListener();
        listener.startPortAndListen(3773);
        }



    }
