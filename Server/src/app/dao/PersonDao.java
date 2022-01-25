package app.dao;

import app.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonDao {


    private static String sqlInsertUser = "INSERT INTO Persons(username,password) VALUES(?,?);";
    private static String sqlRemoveUser = "DELETE FROM Persons WHERE username=?";
    public static void insert(String userName, String password, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlInsertUser);
            stmt.setString(1,userName);
            stmt.setString(2,password);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Inserare esuata!\n");
        }
    }
    public static void delete(String userName, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlRemoveUser);
            stmt.setString(1,userName);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Stergere esuata!\n");
        }
    }
    public static List<Person> read(Connection conn){
        List<Person> persons = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT id, username, id_echipa FROM persons");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                int idEchipa = resultSet.getInt("id_echipa");
                Person person = new Person(id,userName,idEchipa);
                persons.add(person);
            }
        } catch (Exception e){
            System.out.println("Exceptie la read");
        }
        return persons;
    }
}
