package app.dao;

import app.Singleton;
import app.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StageDao {
    private static int stagesNo=0;
    private static String sqlInsertPerson = "INSERT INTO clasament_etapa(username, id_etapa) VALUES(?,?);";
    private static String sqlInsertScore = "UPDATE clasament_etapa SET scor=? WHERE (username=? AND id_etapa=?);";
    public static void insertPersons(Connection conn, int idEtapa){
        try{
            List<Person> persons = PersonDao.read(conn);
            for(Person person : persons) {
                PreparedStatement stmt = conn.prepareStatement(sqlInsertPerson);
                stmt.setString(1, person.getUserName());
                stmt.setInt(2,idEtapa);
                stmt.execute();
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exceptie la insertPersons!\n");
        }
    }

    public static void insertScore(String username, float score, int idEtapa, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlInsertScore);
            stmt.setFloat(1,score);
            stmt.setString(2,username);
            stmt.setInt(3,idEtapa);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exceptie la insertScore!\n");
        }
    }
    public static boolean scorDejaInserat(String username, int idEtapa, Connection conn){
        boolean value=true;
        try{
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT scor FROM clasament_etapa WHERE (username='"+username+"' AND id_etapa="+idEtapa+");");
            while (resultSet.next()) {
                if(resultSet.getInt("scor")==0){
                    value=false;
                }
            }
        } catch (Exception e){
            System.out.println("Exception fct scorDejaInserat\n" + e);
        }
        return value;
    }
    public static boolean toateScorurileSuntInserate(int idEtapa, Connection conn){
        boolean value=true;
        try{
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT scor FROM clasament_etapa WHERE id_etapa="+idEtapa+";");
            while (resultSet.next()) {
                if(resultSet.getInt("scor")==0){
                    value=false;
                }
            }
        } catch (Exception e){
            System.out.println("Exception fct toateScorurileSuntInserate e="+e);
        }
        return value;
    }
    public static void insertPersonsInClasamentFinal(Connection conn){
        try{
            List<Person> persons = PersonDao.read(conn);
            for(Person person : persons) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO clasament_final(id_persoana, username, id_echipa) VALUES(?,?,?);");
                stmt.setInt(1,person.getId());
                stmt.setString(2, person.getUserName());
                stmt.setInt(3,person.getIdEchipa());
                stmt.execute();
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exceptie la insertPersons!\n");
        }
    }
    public static void initClasamentFinal(Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM clasament_final;");
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exceptie la initClasamentFinal!" + e);
        }
    }

    public static void updateClasamentFinal(int idEtapa, Connection conn){
        try{
            List<Float> punctaje = new ArrayList<>();
            List<Integer> locuri = new ArrayList<>();
            List<String> usernameList = new ArrayList<>();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT username, punctaj_primit FROM clasament_etapa WHERE id_etapa="+idEtapa+";");
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                float punctajPrimit = resultSet.getFloat("punctaj_primit");
                usernameList.add(username);
                punctaje.add(punctajPrimit);
            }
            ResultSet resultSet2 = statement.executeQuery("SELECT username, punctaj FROM clasament_final;");
            List<Person> personList = new ArrayList<>();
            while(resultSet2.next()){
                String username = resultSet2.getString("username");
                float punctaj = resultSet2.getFloat("punctaj");
                personList.add(new Person(username,punctaj));
            }
            boolean terminat=false;
            float punctajMaxim=-1;
            int locNou=0;
            for(int index=0; index<punctaje.size(); index++){
                locuri.add(0);
            }
            while(!terminat){
                locNou++;
                punctajMaxim=-1;
                terminat=true;
                for(int index=0; index<punctaje.size(); index++){
                    if(locuri.get(index)==0 && punctajMaxim<punctaje.get(index)){
                        punctajMaxim=punctaje.get(index);
                        terminat=false;
                    }
                }
                for(int index=0; index<punctaje.size(); index++){
                    if(locuri.get(index)==0 && punctajMaxim==punctaje.get(index)){
                         locuri.set(index,locNou);
                    }
                }
            }
            for(int index=0; index<punctaje.size(); index++){
                for(Person person : personList) {
                    if(person.getUserName().equals(usernameList.get(index))) {
                        PreparedStatement stmt1 = conn.prepareStatement("UPDATE clasament_final SET loc=" + (locuri.get(index)) + ", punctaj="+(punctaje.get(index)+person.getPunctajTotal())+" WHERE username='" + (usernameList.get(index)) + "';");
                        stmt1.execute();
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Exception fct scorDejaInserat\n" + e);
        }
    }
    public static void generareLocuri(Connection conn, int idEtapa){
        boolean terminat=false;
        List<Integer> punctaje = new ArrayList<>();
        punctaje.add(10);
        punctaje.add(6);
        punctaje.add(3);
        punctaje.add(1);
        punctaje.add(0);
        int persoaneCuScorMaxim=0;
        int locNou=0;
        while(!terminat){
            terminat=true;
            locNou++;
            int scorMaxim=-1;
            try{
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT username, scor, loc FROM clasament_etapa WHERE id_etapa="+idEtapa+";");
                while (resultSet.next()) {
                    int scor=resultSet.getInt("scor");
                    int loc=resultSet.getInt("loc");
                    if(scorMaxim<scor && loc==0){
                        scorMaxim=scor;
                        persoaneCuScorMaxim=1;
                        terminat=false;
                    } else if(scorMaxim==scor && loc==0){
                        persoaneCuScorMaxim++;
                    }
                }
                float punctajNou=0;
                if(locNou<=4) {
                    punctajNou=(float) punctaje.get(locNou - 1) / persoaneCuScorMaxim;
                } else {
                    punctajNou=0;
                }
                PreparedStatement stmt = conn.prepareStatement("UPDATE clasament_etapa SET punctaj_primit="+punctajNou+", loc="+locNou+" WHERE (scor="+scorMaxim+" AND id_etapa="+idEtapa+");");
                stmt.execute();
            } catch (Exception e){
                System.out.println("Exception fct toateScorurileSuntInserate e="+e);
            }
        }
    }
    public static String getClasamentFinal(Connection conn) {
        String clasament = "";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT username, punctaj, loc, id_echipa FROM clasament_final;");
            while(resultSet.next()){
                String username = resultSet.getString("username");
                float punctajFloat = resultSet.getFloat("punctaj");
                int locInt = resultSet.getInt("loc");
                int idEchipaInt = resultSet.getInt("id_echipa");
                clasament = clasament + "username:" + username + " loc:" + locInt + " punctaj:" + punctajFloat + " idEchipa:" + idEchipaInt+"\n";
            }
        } catch (Exception e){
            System.out.println("Exception getClasamentFinal:" + e);
        }
        return clasament;
    }
    public static void setStagesNo(int stagesNo) {
        StageDao.stagesNo = stagesNo;
    }

    public static int getStagesNo() {
        return stagesNo;
    }
}
