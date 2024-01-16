package com.svalero.downloader.controller;

import java.io.*;
import java.net.URL;
import java.util.*;


import com.svalero.downloader.task.HistorialTask;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppController implements Initializable {

    @FXML
    private Label imagePathLabel;
    @FXML
    private TabPane tpFilters;
    @FXML
    private ListView batchFilterListView;
    @FXML
    private Button showHistorial;

    ObservableList<String> selectedItems;

    private List<HistorialTask> history = new ArrayList<>();


    public AppController(){

    }

    //Inicializamos la ListView con todos los posibles filtros
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.batchFilterListView.getItems().addAll("Grayscale", "Brighter", "Sepia", "Enhance Contrast", "Color Inversion");
        this.batchFilterListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Para poder seleccionar varias opciones
        tpFilters.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    //Seleccionamos el fichero a traves del botón
    @FXML
    public void openImage(ActionEvent event){
        Stage stage = (Stage) this.tpFilters.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File imageFile = fileChooser.showOpenDialog(stage);
        this.imagePathLabel.setText(imageFile.getName());
        createFilter(imageFile, new ArrayList<String>());
    }

    private void createFilter(File imageFile, List<String> selectedFilters){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filter.fxml"));
            //Le pasamos al filterController el fichero seleccionado y los filtros seleccionados
            FilterController filterController = new FilterController(imageFile, selectedFilters);
            loader.setController(filterController);
            VBox filterBox = loader.load();

            //Creamos el historial
            HistorialTask historialTask = new HistorialTask(imageFile.getName(), selectedFilters);
            history.add(historialTask);

            String fileName = imageFile.getName();
            System.out.println(fileName);
            tpFilters.getTabs().add(new Tab(fileName, filterBox));
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    //Metodo para trabajar en modo batch cuando preseleccionamos algun filtro para algun directorio
    @FXML
    public void openBatch(ActionEvent event) {
        List<String> selectedFilters = new ArrayList<String>(
                this.batchFilterListView.getSelectionModel().getSelectedItems());
        DirectoryChooser directoryChooser = new DirectoryChooser();
        //Mirar bien si es tpFilter o tpFilters
        Stage stage = (Stage) this.tpFilters.getScene().getWindow();
        File folder = directoryChooser.showDialog(stage);
        if (folder == null)
            return;

        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null){
            for (File imageFile : listOfFiles){
                if (imageFile.isFile()) {
                    System.out.println("File: " + imageFile.getName());
                    createFilter(imageFile, selectedFilters);
                } else if (imageFile.isDirectory()) {
                    System.out.println("Directory: " + imageFile.getName());

                }
            }

        } else {
            System.out.println("The specified path is not a directory");
        }
    }

    @FXML
    public void exitApplication(ActionEvent event){
        Platform.exit();
    }

    public List<HistorialTask> getHistory(){
        return new ArrayList<>(history);
    }

    @FXML
    public void showHistorial(){
        //Obtenemos el historial
        List<HistorialTask> history = getHistory();

        //Creamos una cadena de caracteres que se concatenará más tarde con el nombre de archivo y filtros
        StringBuilder message = new StringBuilder("History:\n");

        //Recorremos el array recogiendo todos los nombres de los archivos y su selección de filtros correspondientes
        for (HistorialTask entry : history){
            message.append("Archivo: ").append(entry.getImageName()).append("\n");
            message.append("Filtros: ").append(entry.getAppliedFilters()).append("\n\n");
        }

        //Creamos un alert para mostrar el la informacion del historial
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("History");
        alert.setHeaderText(null);
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

}
