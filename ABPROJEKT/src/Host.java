import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    public void Create(){
        System.out.println("Starting server...");
        int portNumber = 1234; // replace with your port number

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Send a welcome message to the client
            out.println("Welcome to the server!");

            String inputLine;
            while (true){
                if ((inputLine = in.readLine()) != null) {

                    System.out.println("Received from client: " + inputLine);
//                    out.println("You said: " + inputLine);
                    if (inputLine.equals("EXIT")) {
                        break;
                    }
                }
            }


            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Host host = new Host();
        host.Create();
    }
}
