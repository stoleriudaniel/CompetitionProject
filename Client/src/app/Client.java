package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main (String[] args) throws IOException {
        String serverAddress = "127.0.0.1"; // The server's IP address
        int PORT = 8100; // The server's port
        try (
                Socket socket = new Socket(serverAddress, PORT);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader (
                        new InputStreamReader(socket.getInputStream())) ) {
            String request = "";
            while(true) {
                String response = "";
                if(request.equals("AFISARE_CLASAMENT_FINAL")){
                    while(true) {
                        String newLine = in.readLine();
                        if(newLine.equals("END")){
                            break;
                        }
                        response = response + newLine + "\n";
                    }
                } else {
                    response = in.readLine();
                }
                System.out.println(response);
                Scanner scanner = new Scanner(System.in);
                request = scanner.nextLine();
                // Send a request to the server
                out.println(request);
                if(request.equals("IESIRE")){
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("No server listening... " + e);
        }
    }
}
