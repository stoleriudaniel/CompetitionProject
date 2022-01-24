package app;

import app.dao.PersonDao;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        PersonDao.delete("Marian",Singleton.getConnection());
    }
}
