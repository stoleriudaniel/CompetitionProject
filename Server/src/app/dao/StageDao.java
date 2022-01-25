package app.dao;

import app.model.Person;

import java.sql.*;
import java.util.List;

public class StageDao {
    private static String sqlInsertPerson = "INSERT INTO clasament_etapa(id_persoana, username) VALUES(?,?);";
    private static String sqlInsertScore = "UPDATE clasament_etapa SET scor=? WHERE (username=? AND id_etapa=?);";
    public static void insertPersons(Connection conn){
        try{
            List<Person> persons = PersonDao.read(conn);
            for(Person person : persons) {
                PreparedStatement stmt = conn.prepareStatement(sqlInsertPerson);
                stmt.setInt(1, person.getId());
                stmt.setString(2, person.getUserName());
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
            stmt.setString(1,username);
            stmt.setFloat(2,score);
            stmt.setInt(3,idEtapa);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exceptie la insertScore!\n");
        }
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
