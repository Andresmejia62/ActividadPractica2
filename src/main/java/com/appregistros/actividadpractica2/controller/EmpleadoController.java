package com.appregistros.actividadpractica2.controller;

import com.appregistros.actividadpractica2.DBconnection.dbConnection;
import com.appregistros.actividadpractica2.model.Empleado;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoController {
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtCargo;
    @FXML
    private TextField txtSalario;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;

    @FXML
    private ComboBox<String> cmbFiltro1;
    @FXML
    private ComboBox<String> cmbFiltro2;
    @FXML
    private ComboBox<String> cmbFiltro3;
    @FXML
    private TextField txtFiltro1;
    @FXML
    private TextField txtFiltro2;
    @FXML
    private TextField txtFiltro3;

    @FXML
    private TableView<Empleado> tblEmpleados;
    @FXML
    private TableColumn<Empleado, String> colCodigo;
    @FXML
    private TableColumn<Empleado, String> colNombre;
    @FXML
    private TableColumn<Empleado, String> colCorreo;
    @FXML
    private TableColumn<Empleado, String> colCargo;
    @FXML
    private TableColumn<Empleado, Double> colSalario;

    private final ObservableList<Empleado> empleados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCorreo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCorreo()));
        colCargo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCargo()));
        colSalario.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSalario()).asObject());
        tblEmpleados.setItems(empleados);
        CargarEmpleados();
    }

    private Empleado crearEmpleadoDesdeResultSet(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setId_empleado(rs.getInt("id_empleado"));
        empleado.setCodigo(rs.getString("codigo"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setCorreo(rs.getString("correo"));
        empleado.setCargo(rs.getString("cargo"));
        empleado.setSalario(rs.getDouble("salario"));
        return empleado;
    }

    @FXML
    private void BuscarEmpleado() {
        buscarEmpleados();
    }

    @FXML
    private void QuitarFiltros() {
        if (txtFiltro1 != null) txtFiltro1.clear();
        if (txtFiltro2 != null) txtFiltro2.clear();
        if (txtFiltro3 != null) txtFiltro3.clear();
        if (cmbFiltro1 != null) cmbFiltro1.setValue(null);
        if (cmbFiltro2 != null) cmbFiltro2.setValue(null);
        if (cmbFiltro3 != null) cmbFiltro3.setValue(null);
        CargarEmpleados();
    }

    private void buscarEmpleados() {
        empleados.clear();

        StringBuilder sql = new StringBuilder("""
                SELECT id_empleado, codigo, nombre, correo, cargo, salario
                FROM empleados
                WHERE activo = true
                """);

        List<Object> parametros = new ArrayList<>();

        agregarFiltro(sql, parametros, txtFiltro1, cmbFiltro1);
        agregarFiltro(sql, parametros, txtFiltro2, cmbFiltro2);
        agregarFiltro(sql, parametros, txtFiltro3, cmbFiltro3);

        sql.append(" ORDER BY id_empleado");

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                Object valor = parametros.get(i);
                if (valor instanceof LocalDate fecha) {
                    statement.setDate(i + 1, Date.valueOf(fecha));
                } else {
                    statement.setObject(i + 1, valor);
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    empleados.add(crearEmpleadoDesdeResultSet(resultSet));
                }
            }

            tblEmpleados.setItems(empleados);

            if (empleados.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sin resultados");
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo realizar la búsqueda.");
            e.printStackTrace();
        }
    }

    private void agregarFiltro(StringBuilder sql, List<Object> parametros, TextField txtFiltro, ComboBox<String> comboBox) {
        if (txtFiltro == null || comboBox == null) {
            return;
        }

        String valor = txtFiltro.getText() == null ? "" : txtFiltro.getText().trim();
        if (valor.isEmpty()) {
            return;
        }

        String columna = comboBox.getValue();
        if (columna == null) {
            return;
        }

        switch (columna) {
            case "Código" -> {
                sql.append(" AND codigo ILIKE ?");
                parametros.add("%" + valor + "%");
            }
            case "Nombre" -> {
                sql.append(" AND nombre ILIKE ?");
                parametros.add("%" + valor + "%");
            }
            case "Correo" -> {
                sql.append(" AND correo ILIKE ?");
                parametros.add("%" + valor + "%");
            }
            case "Cargo" -> {
                sql.append(" AND cargo ILIKE ?");
                parametros.add("%" + valor + "%");
            }
            case "Salario" -> {
                try {
                    double salario = Double.parseDouble(valor);
                    sql.append(" AND salario = ?");
                    parametros.add(salario);
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "El valor del salario debe ser numérico.");
                }
            }
            default -> {
            }
        }
    }

    private void CargarEmpleados() {
        System.out.println("CargarEmpleados invoked");
        empleados.clear();

        String sql = "SELECT id_empleado, codigo, nombre, correo, cargo, salario " +
                "FROM empleado " +
                "WHERE activo = true " +
                "ORDER BY id_empleado";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Empleado empleado = new Empleado();
                empleado.setId_empleado(resultSet.getInt("id_empleado"));
                empleado.setCodigo(resultSet.getString("codigo"));
                empleado.setNombre(resultSet.getString("nombre"));
                empleado.setCorreo(resultSet.getString("correo"));
                empleado.setCargo(resultSet.getString("cargo"));
                empleado.setSalario(resultSet.getDouble("salario"));
                empleados.add(empleado);
            }

            tblEmpleados.setItems(empleados);
        } catch (SQLException e) {
            System.out.println("error al cargar empleados: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar los empleados.");
        }
    }

    private boolean validarFormulario() {
        if (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El código es obligatorio.");
            return false;
        }
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El nombre es obligatorio.");
            return false;
        }
        if (txtCorreo.getText() == null || txtCorreo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El correo es obligatorio.");
            return false;
        }
        if (txtCargo.getText() == null || txtCargo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El cargo es obligatorio.");
            return false;
        }
        if (txtSalario.getText() == null || txtSalario.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El salario es obligatorio.");
            return false;
        }

        try {
            Double.parseDouble(txtSalario.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "El salario debe ser un número válido.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void Guardar() {
        if (!validarFormulario()) {
            return;
        }

        String sql = "INSERT INTO empleado (codigo, nombre, correo, cargo, salario) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, txtCorreo.getText().trim());
            statement.setString(4, txtCargo.getText().trim());
            statement.setDouble(5, Double.parseDouble(txtSalario.getText().trim()));

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                Empleado empleado = new Empleado();
                empleado.setCodigo(txtCodigo.getText().trim());
                empleado.setNombre(txtNombre.getText().trim());
                empleado.setCorreo(txtCorreo.getText().trim());
                empleado.setCargo(txtCargo.getText().trim());
                empleado.setSalario(Double.parseDouble(txtSalario.getText().trim()));
                empleados.add(empleado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado guardado correctamente.");
                Limpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el empleado.");
            }
        } catch (SQLException ex) {
            System.out.println("error al guardar empleado: " + ex.getMessage());
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el empleado. Revisa los datos e intenta nuevamente.");
        }
    }

    @FXML
    private void Actualizar() {
        Empleado empleado = tblEmpleados.getSelectionModel().getSelectedItem();
        if (empleado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un empleado para actualizar.");
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        String sql = "UPDATE empleado SET codigo = ?, nombre = ?, correo = ?, cargo = ?, salario = ? WHERE id_empleado = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, txtCorreo.getText().trim());
            statement.setString(4, txtCargo.getText().trim());
            statement.setDouble(5, Double.parseDouble(txtSalario.getText().trim()));
            statement.setInt(6, empleado.getId_empleado());

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                empleado.setCodigo(txtCodigo.getText().trim());
                empleado.setNombre(txtNombre.getText().trim());
                empleado.setCorreo(txtCorreo.getText().trim());
                empleado.setCargo(txtCargo.getText().trim());
                empleado.setSalario(Double.parseDouble(txtSalario.getText().trim()));
                tblEmpleados.refresh();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado actualizado correctamente.");
                Limpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo actualizar el empleado.");
            }
        } catch (SQLException ex) {
            System.out.println("error al actualizar empleado: " + ex.getMessage());
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo actualizar el empleado. Revisa los datos e intenta nuevamente.");
        }
    }

    @FXML
    private void Eliminar() {
        Empleado empleado = tblEmpleados.getSelectionModel().getSelectedItem();
        if (empleado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un empleado para eliminar.");
            return;
        }

        String sql = "UPDATE empleado SET estado = 'inactivo' WHERE id_empleado = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, empleado.getId_empleado());

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                empleados.remove(empleado);
                tblEmpleados.refresh();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado eliminado correctamente.");
                Limpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el empleado.");
            }
        } catch (SQLException ex) {
            System.out.println("error al eliminar empleado: " + ex.getMessage());
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el empleado. Revisa los datos e intenta nuevamente.");
        }
    }

    @FXML
    private void Limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtCargo.clear();
        txtSalario.clear();
        tblEmpleados.getSelectionModel().clearSelection();
    }



}



