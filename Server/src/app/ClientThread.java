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
        else if(autentificat && command.equals("AFISARE_CLASAMENT_FINAL")){
            afisareClasamentFinal();
        }
        else if(autentificat && command.equals("AFISARE_CLASAMENT_ETAPA")){
            afisareClasamentEtapa();
        }
        else if(autentificat && adminLogged && command.equals("INSERARE_NR_ETAPE")){
            inserareNrEtape();
        }
        else if(autentificat && adminLogged && command.equals("START_COMPETITIE")){
            startCompetitie();
        }
        else if(autentificat && adminLogged && command.equals("GENERARE_CLASAMENT_ETAPA")){
            generareClasamentEtapa();
        }
        else if(autentificat && adminLogged && command.equals("GENERARE_CLASAMENT_FINAL")){
            generareClasamentFinal();
        }
        else if(autentificat && adminLogged && command.equals("GOLIRE_PERSOANE")){
            golirePersoane();
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
        if(PersonDao.userExists(username, Singleton.getConnection())) {
            mesajServer = "[Server] Inregistrare esuata! Userul exista deja. Introduceti comanda de [INREGISTRARE], [AUTENTIFICARE] sau [IESIRE]:";
        } else if(password.length()==0){
            mesajServer = "[Server] Parola invalida! Introduceti comanda de [INREGISTRARE], [AUTENTIFICARE] sau [IESIRE]:";
        } else if(PersonDao.numarPersoaneEchipa(idEchipaInt,Singleton.getConnection())>=5){
            mesajServer = "[Server] Inregistrare esuata! Echipa este plina (exista deja 5 persoane). Introduceti comanda de [INREGISTRARE], [AUTENTIFICARE] sau [IESIRE]:";
        }
        else {
            PersonDao.insert(username,password, idEchipaInt, Singleton.getConnection());
            mesajServer = "[Server] Inregistrat cu succes! Introduceti comanda de [INREGISTRARE], [AUTENTIFICARE] sau [IESIRE]:";
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
            mesajServer = "[Server] Autentificat cu succes ca si participant! Introduceti comanda de [IESIRE], [DELOGARE], [INSERARE_SCOR], [AFISARE_CLASAMENT_FINAL], [AFISARE_CLASAMENT_ETAPA]";
            autentificat=true;
            usernameLogged=username;
        } else if (PersonDao.isAdmin(username,password,Singleton.getConnection())){
            mesajServer = "[Server] Autentificat cu succes ca si admin! Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [AFISARE_CLASAMENT_FINAL], [AFISARE_CLASAMENT_ETAPA]";
            adminLogged=true;
            autentificat=true;
            usernameLogged=username;
        } else {
            mesajServer = "[Server] Autentificare esuata! Introduceti comanda de [INREGISTRARE], [AUTENTIFICARE] sau [IESIRE]:";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void delogare() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Delogat cu succes! Introduceti comanda de [INREGISTRARE], [AUTENTIFICARE] sau [IESIRE]";
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
//        for(int indexEtapa=1; indexEtapa<=nrEtapeInt; indexEtapa++){
//            StageDao.insertPersons(Singleton.getConnection(),indexEtapa);
//        }
        mesajServer = "[Server] Inserarea a avut succes! Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [GOLIRE_PERSOANE], [AFISARE_CLASAMENT_ETAPA], [AFISARE_CLASAMENT_FINAL]";
        out.println(mesajServer);
        out.flush();
    }
    public void startCompetitie() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] ";
        if(StageDao.getStagesNo()==0){
            mesajServer = mesajServer + "Eroare. Numarul de etape este 0. Setati numarul de etape folosind comanda [INSERARE_NR_ETAPE].";
        }
        else {
            mesajServer = mesajServer + "Competitia a inceput. Asteptam ca participantii sa isi introduca scorul. Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [AFISARE_CLASAMENT_ETAPA], [AFISARE_CLASAMENT_FINAL]";
            // golim tabelele clasament_etapa si clasament_final pentru noua competitie
            StageDao.initClasamentEtapa(Singleton.getConnection());
            StageDao.initClasamentFinal(Singleton.getConnection());
            for(int indexEtapa=1; indexEtapa<=StageDao.getStagesNo(); indexEtapa++){
                //inseram pentru fiecare etapa, persoanele in clasament_etapa pentru ca mai apoi sa isi introduca scorul
                StageDao.insertPersons(Singleton.getConnection(),indexEtapa);
            }
        }
        out.println(mesajServer);
        out.flush();
    }
    public void generareClasamentEtapa() throws IOException {
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
            mesajServer = mesajServer + "Au fost actualizate " + etapeActualizate + " etape. Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [GOLIRE_PERSOANE], [AFISARE_CLASAMENT_ETAPA], [AFISARE_CLASAMENT_FINAL]";
        } else {
            mesajServer = mesajServer + "Nu a fost actualizata nicio etapa. Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [GOLIRE_PERSOANE], [AFISARE_CLASAMENT_ETAPA], [AFISARE_CLASAMENT_FINAL]";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void generareClasamentFinal() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        StageDao.initClasamentFinal(Singleton.getConnection());
        StageDao.insertPersonsInClasamentFinal(Singleton.getConnection());
        System.out.println("stages:" + StageDao.getStagesNo());
        for(int etapa=1; etapa<=StageDao.getStagesNo(); etapa++) {
            System.out.println("Etapa="+etapa+"boolean: " + StageDao.toateScorurileSuntInserate(etapa, Singleton.getConnection()));
            if(StageDao.toateScorurileSuntInserate(etapa, Singleton.getConnection())) {
                StageDao.updateClasamentFinal(etapa, Singleton.getConnection());
            }
        }
        String mesajServer = "[Server] Actualizat cu succes! Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [GOLIRE_PERSOANE], [AFISARE_CLASAMENT_ETAPA], [AFISARE_CLASAMENT_FINAL]";
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
            mesajServer = "[Server] Scorul a fost introdus! Introduceti comanda de [IESIRE], [DELOGARE], [INSERARE_SCOR], [AFISARE_CLASAMENT_FINAL], [AFISARE_CLASAMENT_ETAPA]";
        } else {
            mesajServer = "[Server] Eroare! Scorul a fost introdus deja sau id-ul etapei este gresit. Introduceti comanda de [IESIRE], [DELOGARE], [INSERARE_SCOR], [AFISARE_CLASAMENT_FINAL], [AFISARE_CLASAMENT_ETAPA]";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void afisareClasamentFinal() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Clasamentul final este:\n";
        String clasament = StageDao.getClasamentFinal(Singleton.getConnection());
        boolean toateEtapeleSuntComplete=true;
        for(int indexEtapa=1; indexEtapa<=StageDao.getStagesNo(); indexEtapa++){
            if(!StageDao.toateScorurileSuntInserate(indexEtapa,Singleton.getConnection())){
                toateEtapeleSuntComplete=false;
            }
        }
        if(clasament.length()>0 && toateEtapeleSuntComplete) {
            mesajServer = mesajServer + clasament + "\nEND";
        } else {
            mesajServer="[Server] Nu a fost actualizat clasamentul de catre admin sau adminul nu a inserat numarul de etape sau nu toate etapele sunt complete.\nEND";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void afisareClasamentEtapa() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Introdu id-ul etapei:";
        out.println(mesajServer);
        out.flush();
        String idEtapa = in.readLine();
        int idEtapaInt = Integer.parseInt(idEtapa);
        if(StageDao.toateScorurileSuntInserate(idEtapaInt, Singleton.getConnection())) {
            mesajServer = "[Server] Clasamentul etapa dupa id-ul " + idEtapa + " este:\n";
            String clasament = StageDao.getClasamentEtapa(idEtapaInt, Singleton.getConnection());
            mesajServer = mesajServer + clasament + "\nEND";
        } else {
            mesajServer = "[Server] Nu a fost actualizat clasamentul de catre admin sau adminul nu a introdus numarul de etape sau nu sunt toate scorurile inserate.\nEND";
        }
        out.println(mesajServer);
        out.flush();
    }
    public void golirePersoane() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String mesajServer = "[Server] Toate conturile au fost sterse! Introduceti comanda de [INSERARE_NR_ETAPE], [START_COMPETITIE], [GENERARE_CLASAMENT_ETAPA], [GENERARE_CLASAMENT_FINAL], [GOLIRE_PERSOANE], [AFISARE_CLASAMENT_ETAPA], [AFISARE_CLASAMENT_FINAL]";
        PersonDao.clear(Singleton.getConnection());
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
        String mesajServer = "[Server] Comanda invalida!";
        out.println(mesajServer);
        out.flush();
    }
}