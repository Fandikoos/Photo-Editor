package com.svalero.downloader.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


import com.svalero.downloader.task.HistorialTask;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppController implements Initializable {

    @FXML
    private Label infoImageLabel;
    @FXML
    private Button btOpenImage;
    @FXML
    private Button btCreateFilter;
    @FXML
    private TabPane tpFilter;
    @FXML
    private ListView filterListView;
    @FXML
    private Button showHistorial;

    ObservableList<String> selectedItems;

    private File file;
    private List<HistorialTask> history = new ArrayList<>();

    public AppController(){

    }

    //Inicializamos la ListView con todos los posibles filtros
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tpFilter.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        this.filterListView.getItems().addAll("Grayscale", "Brighter", "Sepia", "Enhance Contrast", "Color Inversion");
        //Para poder seleccionar varias opciones
        this.filterListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    //Seleccionamos el fichero a traves del botón
    @FXML
    public void openImage(ActionEvent event){
        Stage stage = (Stage) this.btOpenImage.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        this.file = fileChooser.showOpenDialog(stage);
        this.infoImageLabel.setText(this.file.getName());
    }

    @FXML
    private void createFilter(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filter.fxml"));
            System.out.println(this.filterListView.getSelectionModel().getSelectedItems());
            List<String> selectedFilters = new ArrayList<String>(this.filterListView.getSelectionModel().getSelectedItems());

            //Creamos el historial
            HistorialTask historialTask = new HistorialTask(file.getName(), selectedFilters);
            history.add(historialTask);

            //Le pasamos al filterController el fichero seleccionado y los filtros seleccionados
            FilterController filterController = new FilterController(file, selectedFilters);
            loader.setController(filterController);
            VBox filterBox = loader.load();

            String fileName = file.getName();
            System.out.println(fileName);
            tpFilter.getTabs().add(new Tab(fileName, filterBox));
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
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
