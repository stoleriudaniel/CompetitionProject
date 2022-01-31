package app.dao;

import app.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EchipaDao {
    public static void delete(int idEchipa, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM persons WHERE id_echipa="+idEchipa);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Stergere esuata!\n");
        }
    }
    public static void updateIdEchipa(int idEchipaVechi, int idEchipaNou, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement("UPDATE persons SET id_echipa="+idEchipaNou+" WHERE id_echipa="+idEchipaVechi);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Stergere esuata!\n");
        }
    }
    public static List<Person> read(int idEchipa, Connection conn){
        List<Person> persons = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT id, username, id_echipa FROM persons WHERE id_Echipa="+idEchipa);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                int idEchipaInt = resultSet.getInt("id_echipa");
                Person person = new Person(id,userName,idEchipaInt);
                persons.add(person);
            }
        } catch (Exception e){
            System.out.println("Exceptie la read");
            System.out.println(e.getCause());
        }
        return persons;
    }
}
