package com.svalero.downloader.task;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.sql.*;

public class SQLiteDB {
    private static final String JDBC_URL = "jdbc:sqlite:historial.db";


    public static void initializeDatabase() {
        //Conectamos con la BBDD y creamos nuestra tabla SQL
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS historial (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nameFile TEXT," +
                    "selectedFilters TEXT)";
            try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Insertamos un registro en la tabla historial
    public static void insertHistorial(String nameFile, String selectedFilters) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            //Consulta insert SQL con los dos parametros (nombre del archivo y los filtros)
            String insertSQL = "INSERT INTO historial (nameFile, selectedFilters) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setString(1, nameFile);
                statement.setString(2, selectedFilters);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Recuperamos todos los registros de la tabla historial
    public static void showHistorial(TextArea textArea) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            // Consulta SQL para seleccionar todos los registros
            String selectSQL = "SELECT * FROM historial";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Iteramos mientras haya una fila siguiente hasta que ya no haya
                    while (resultSet.next()) {
                        //Los pintamos en el TextArea
                        textArea.appendText("ID: " + resultSet.getInt("id") + "\n");
                        textArea.appendText("Archivo: " + resultSet.getString("nameFile") + "\n");
                        textArea.appendText("Filtros: " + resultSet.getString("selectedFilters") + "\n");
                        textArea.appendText("\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
