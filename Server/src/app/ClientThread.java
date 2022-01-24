package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientThread extends Thread {
    private Socket socket = null ;
    private boolean connected=true;
    private boolean autentificat=false;
    private boolean welcomeMessageIsPrinted=false;
    private String welcomeMessage = "[Server] Bun venit! Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
    public ClientThread (Socket socket) throws IOException { this.socket = socket ; }
    public void run () {
        try {
            while(connected) {
                // Get the request from the input stream: client → server
                // Send the response to the oputput stream: server → client
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                if(welcomeMessageIsPrinted==false) {
                    out.println(welcomeMessage);
                    out.flush();
                    String comandaClient = in.readLine();
                    handleCommand(comandaClient);
                    welcomeMessageIsPrinted=true;
                }
                else {
                    String comandaClient = in.readLine();
                    handleCommand(comandaClient);
                }
                while(autentificat){
                    String comandaClient = in.readLine();
                    handleCommand(comandaClient);
                }
            }
        } catch (IOException e) {
            System.err.println("Communication error... " + e);
        } finally {
            try {
                socket.close(); // or use try-with-resources
            } catch (IOException e) { System.err.println (e); }
        }
    }
    public void handleCommand(String command) throws IOException {
        if(!autentificat && command.equals("INREGISTRARE")){
            inregistrare();
        }
        else if(!autentificat && command.equals("AUTENTIFICARE")){
            autentificare();
        }
        else if(autentificat && command.equals("DELOGARE")){
             delogare();
        }
        else if(command.equals("IESIRE")){
            iesire();
        }
        else comandaInvalida();
    }
    public void inregistrare() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Introduceti username-ul:";
        out.println(mesajServer);
        out.flush();
        String mesajClient = in.readLine();
        mesajServer = "[Server] Introduceti parola:";
        out.println(mesajServer);
        out.flush();
        mesajClient = in.readLine();
        mesajServer = "[Server] Inregistrat cu succes! Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
        out.println(mesajServer);
        out.flush();
    }
    public void autentificare() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Autentificat cu succes! Introduceti comanda:";
        autentificat=true;
        out.println(mesajServer);
        out.flush();
    }
    public void delogare() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Delogat cu succes! Introduceti comanda:";
        autentificat=false;
        out.println(mesajServer);
        out.flush();
    }
    public void iesire() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Iesire...";
        out.println(mesajServer);
        out.flush();
        connected=false;
    }
    public void comandaInvalida() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Comanda invalida! Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
        out.println(mesajServer);
        out.flush();
    }
}