package app;

import app.dao.PersonDao;
import app.dao.StageDao;
import app.model.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;

class ClientThread extends Thread {
    private Socket socket = null ;
    private boolean connected=true;
    private boolean autentificat=false;
    private boolean welcomeMessageIsPrinted=false;
    private String usernameLogged="";
    private boolean adminLogged = false;
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
        else if(autentificat && command.equals("INSERARE_SCOR")){
            inserareScor();
        }
        else if(autentificat && adminLogged && command.equals("INSERARE_NR_ETAPE")){
            inserareNrEtape();
        }
        else if(autentificat && adminLogged && command.equals("CLASAMENT_ETAPA")){
            clasamentEtapa();
        }
        else if(autentificat && adminLogged && command.equals("CLASAMENT_FINAL")){
            clasamentFinal();
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
        String username = in.readLine();
        mesajServer = "[Server] Introduceti parola:";
        out.println(mesajServer);
        out.flush();
        String password = in.readLine();
        mesajServer = "[Server] Introduceti id-ul echipei:";
        out.println(mesajServer);
        out.flush();
        String idEchipaString = in.readLine();
        int idEchipaInt = Integer.parseInt(idEchipaString);
        System.out.println("nrMembrii: id=" + idEchipaInt +" " + PersonDao.numarPersoaneEchipa(idEchipaInt,Singleton.getConnection()));
        if(PersonDao.userExists(username, Singleton.getConnection())) {
            mesajServer = "[Server] Inregistrare esuata! Userul exista deja. Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
        } else if(password.length()==0){
            mesajServer = "[Server] Parola invalida! Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
        } else if(PersonDao.numarPersoaneEchipa(idEchipaInt,Singleton.getConnection())>=5){
            mesajServer = "[Server] Inregistrare esuata! Echipa este plina. Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
        }
        else {
            PersonDao.insert(username,password, idEchipaInt, Singleton.getConnection());
            mesajServer = "[Server] Inregistrat cu succes!";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void autentificare() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Introduceti username-ul:";
        out.println(mesajServer);
        out.flush();
        String username = in.readLine();
        mesajServer = "[Server] Introduceti parola:";
        out.println(mesajServer);
        out.flush();
        String password = in.readLine();
        if(PersonDao.isValidAccount(username,password,Singleton.getConnection())) {
            mesajServer = "[Server] Autentificat cu succes ca si participant!";
            autentificat=true;
            usernameLogged=username;
        } else if (PersonDao.isAdmin(username,password,Singleton.getConnection())){
            mesajServer = "[Server] Autentificat cu succes ca si admin!";
            adminLogged=true;
            autentificat=true;
            usernameLogged=username;
        } else {
            mesajServer = "[Server] Autentificare esuata! Introduceti comanda de INREGISTRARE, AUTENTIFICARE sau IESIRE:";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void delogare() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Delogat cu succes! Introduceti comanda:";
        autentificat=false;
        usernameLogged="";
        adminLogged=false;
        out.println(mesajServer);
        out.flush();
    }
    public void inserareNrEtape() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Introduceti numarul de etape:";
        out.println(mesajServer);
        out.flush();
        String nrEtapeString = in.readLine();
        int nrEtapeInt = Integer.parseInt(nrEtapeString);
        StageDao.setStagesNo(nrEtapeInt);
        for(int indexEtapa=1; indexEtapa<=nrEtapeInt; indexEtapa++){
            StageDao.insertPersons(Singleton.getConnection(),indexEtapa);
        }
        mesajServer = "[Server] Inserarea a avut succes!";
        out.println(mesajServer);
        out.flush();
    }
    public void clasamentEtapa() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] ";
        int etapeActualizate=0;
        for(int i=1; i<=StageDao.getStagesNo(); i++) {
            if (StageDao.toateScorurileSuntInserate(i, Singleton.getConnection())) {
                etapeActualizate++;
                StageDao.generareLocuri(Singleton.getConnection(), i);
            }
        }
        if(etapeActualizate>0){
            mesajServer = mesajServer + "Au fost actualizate " + etapeActualizate + " etape.";
        } else {
            mesajServer = mesajServer + "Nu a fost actualizata nicio etapa.";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void clasamentFinal() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        StageDao.initClasamentFinal(Singleton.getConnection());
        StageDao.insertPersonsInClasamentFinal(Singleton.getConnection());
        StageDao.setStagesNo(2);
        System.out.println("stages:" + StageDao.getStagesNo());
        for(int etapa=1; etapa<=StageDao.getStagesNo(); etapa++) {
            System.out.println("Etapa="+etapa+"boolean: " + StageDao.toateScorurileSuntInserate(etapa, Singleton.getConnection()));
            if(StageDao.toateScorurileSuntInserate(etapa, Singleton.getConnection())) {
                StageDao.updateClasamentFinal(etapa, Singleton.getConnection());
            }
        }
        String mesajServer = "[Server] Actualizat cu succes!";
        out.println(mesajServer);
        out.flush();
    }
    public void inserareScor() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Introduceti id-ul etapei:";
        out.println(mesajServer);
        out.flush();
        String idEtapaString = in.readLine();
        int idEtapaInt = Integer.parseInt(idEtapaString);
        mesajServer = "[Server] Introduceti scorul:";
        out.println(mesajServer);
        out.flush();
        String scorString = in.readLine();
        float scorFloat = Float.parseFloat(scorString);
        if(!StageDao.scorDejaInserat(usernameLogged,idEtapaInt,Singleton.getConnection())) {
            StageDao.insertScore(usernameLogged, scorFloat, idEtapaInt, Singleton.getConnection());
            mesajServer = "[Server] Scorul a fost introdus!";
        } else {
            mesajServer = "[Server] Eroare!";
        }
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