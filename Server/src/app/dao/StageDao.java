package app.dao;

import app.model.Person;

import java.sql.*;
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
            ResultSet resultSet = statement.executeQuery("SELECT scor, FROM clasament_etapa WHERE id_etapa="+idEtapa+";");
            while (resultSet.next()) {
                if(resultSet.getInt("scor")==0){
                    value=false;
                }
            }
        } catch (Exception e){
            System.out.println("Exception fct toateScorurileSuntInserate\n");
        }
        return value;
    }
}
