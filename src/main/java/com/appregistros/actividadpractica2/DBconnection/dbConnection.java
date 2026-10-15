package com.appregistros.actividadpractica2.DBconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {


    private static final String URL = "jdbc:postgresql://localhost:5432/bdempleados";
    private static final String USER = "andresmejia";
    private static final String PASSWORD = "amejia62";

    private dbConnection(){



    }

    public static Connection getConnection () throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL no encontrado.", e);
        }
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}
