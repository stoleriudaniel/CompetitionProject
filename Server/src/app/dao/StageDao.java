package app.dao;

import app.model.Person;

import java.sql.*;
import java.util.List;

public class StageDao {
    private static String sqlInsertPerson = "INSERT INTO clasament_etapa(id_persoana, username) VALUES(?,?);";
    private static String sqlInsertScore = "INSERT INTO clasament_etapa(id_persoana, username, scor) VALUES(?,?,?);";
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
            System.out.println("Inserare esuata!\n");
        }
    }

    public static void insertScore(int idPersoana, String username, float score, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlInsertScore);
            stmt.setInt(1,idPersoana);
            stmt.setString(2,username);
            stmt.setFloat(3,score);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Inserare esuata!\n");
        }
    }

    public static boolean toateScorurileSuntInserate(Connection conn){
        boolean value=true;
        try{
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT scor FROM clasament_etapa");

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
