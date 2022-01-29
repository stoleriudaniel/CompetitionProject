package app.dao;

import app.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StageDao {
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
                    System.out.println("resultSet score=" + resultSet.getInt("scor"));
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
}
