package app.dao;

import java.sql.*;

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
    public static void read(Connection conn){
        try {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM persons");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
