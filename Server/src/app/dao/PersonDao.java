package app.dao;

import app.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonDao {


    private static String sqlInsertUser = "INSERT INTO Persons(username,password,id_echipa) VALUES(?,?,?);";
    private static String sqlRemoveUser = "DELETE FROM Persons WHERE username=?";
    private static String sqlInsertAdmin = "INSERT INTO admin(username, password) VALUES(?,?);";
    public static void createPersonsTable(Connection conn){
        try{
            List<Person> persons = PersonDao.read(conn);
            for(Person person : persons) {
                PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS persons (id int, PRIMARY_KEY AUTO_INCREMENT, username varchar(20), password varchar(20), id_echipa INT);");
                stmt.execute();
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exceptie la CreateTablePerons!\n");
        }
    }
    public static void insert(String userName, String password, int idEchipa, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlInsertUser);
            stmt.setString(1,userName);
            stmt.setString(2,password);
            stmt.setInt(3,idEchipa);
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
    public static void clear(Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM persons");
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
            System.out.println(e.getCause());
        }
        return persons;
    }
    public static void insertAdmin(String userName, String password, Connection conn){
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlInsertAdmin);
            stmt.setString(1,userName);
            stmt.setString(2,password);
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Inserare esuata!\n");
            System.out.println(e.getCause());
        }
    }
    public static boolean isAdmin(String userName, String password, Connection conn){
        String userNameAdmin="";
        String passwordAdmin ="";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT username, password FROM admin");

            while (resultSet.next()) {
                userNameAdmin = resultSet.getString("username");
                passwordAdmin = resultSet.getString("password");
            }
        } catch (Exception e){
            System.out.println("Exceptie la isAdmin");
        }
        return (userName.equals(userNameAdmin) && password.equals(passwordAdmin));
    }
    public static boolean isValidAccount(String username, String password, Connection conn){
        boolean validAcc=false;
        try {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT username, password FROM persons");

            while (resultSet.next()) {
                String usernameFound = resultSet.getString("username");
                String passwordFound = resultSet.getString("password");
                if(usernameFound.equals(username) && passwordFound.equals(password)){
                    validAcc=true;
                }
            }
        } catch (Exception e){
            System.out.println("Exceptie la isValidAccount");
            System.out.println(e.getCause());
        }
        return validAcc;
    }

    public static boolean userExists(String username, Connection conn){
        boolean exists=false;
        try {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT username FROM persons");

            while (resultSet.next()) {
                String usernameFound = resultSet.getString("username");
                if(usernameFound.equals(username)){
                    exists=true;
                }
            }
        } catch (Exception e){
            System.out.println("exceptie userExists");
            System.out.println(e.getCause());
        }
        return exists;
    }

    public static int numarPersoaneEchipa(int idEchipa, Connection conn){
        int count=0;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id_echipa FROM persons");
            while (resultSet.next()) {
                int idFound = resultSet.getInt("id_echipa");
                if(idEchipa == idFound){
                    count++;
                }
            }
        } catch (Exception e){
            System.out.println("Exceptie la numarPersoaneEchipa");
            System.out.println(e.getCause());
        }
        return count;
    }
}
