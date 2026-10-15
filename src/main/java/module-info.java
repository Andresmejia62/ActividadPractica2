module com.appregistros.actividadpractica2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.appregistros.actividadpractica2 to javafx.fxml;
    opens com.appregistros.actividadpractica2.controller to javafx.fxml;
    exports com.appregistros.actividadpractica2;
    exports com.appregistros.actividadpractica2.DBconnection;
    exports com.appregistros.actividadpractica2.controller;
    exports com.appregistros.actividadpractica2.model;
}