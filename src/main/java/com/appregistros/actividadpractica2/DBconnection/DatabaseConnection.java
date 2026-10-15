package com.appregistros.actividadpractica2.DBconnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return dbConnection.getConnection();
    }
}
