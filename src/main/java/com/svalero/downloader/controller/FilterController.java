package com.svalero.downloader.controller;

import com.svalero.downloader.task.FilterTask;
import com.svalero.downloader.task.SQLiteDB;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javafx.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FilterController implements Initializable {
    @FXML
    private Label lbStatus;
    @FXML
    private ProgressBar pbProgressBar;
    @FXML
    private ImageView sourceImageView;
    @FXML
    private ImageView targetImageView;
    @FXML
    private Button saveButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button applyFilterButton;
    @FXML
    private ListView filterListView;

    ObservableList<String> selectedItems;

    private File sourceImageFile;
    private List<String> selectedFilters;
    private FilterTask filterTask;
    private BufferedImage outputImage;
    private BufferedImage sourceImage;
    private Image workingImage;

    //Constructor de nuestra clase y en el que recogeremos la imagen seleccionada y el filtro
    public FilterController(File sourceImageFile, List<String> selectedFilters){
        this.sourceImageFile = sourceImageFile;
        this.outputImage = null;
        this.selectedFilters = selectedFilters;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.filterListView.getItems().addAll("Grayscale", "Sepia", "Brigther", "Color Inversion", "Enhance Contrast");
        this.filterListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        InputStream stream;
        try {
            //Cargamos la imagen que hemos seleccionado en la interfaz gráfica
            stream = new FileInputStream(this.sourceImageFile.getAbsolutePath());
            //Creamos la imagen
            Image image = new Image(stream);
            //La conectamos a la interfaz gráfica
            this.sourceImageView.setImage(image);
            this.sourceImage = ImageIO.read(this.sourceImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.saveButton.setDisable(true);
        this.undoButton.setDisable(true);
        this.redoButton.setDisable(true);
        //No trabajamos en modo batch porque no hay ningun filtro seleccionado
        if (this.selectedFilters.size() > 0) {
            applyBatchFilters();
        }
    }

    private void applyBatchFilters(){
        this.undoButton.setDisable(true);
        this.redoButton.setDisable(true);
        this.saveButton.setDisable(true);

        try {
            filterTask = new FilterTask(this.sourceImage, this.selectedFilters);

            //Linkeamos la barra de progreso al progreso que lleva nuestra tarea
            pbProgressBar.progressProperty().unbind();
            pbProgressBar.progressProperty().bind(filterTask.progressProperty());

            //Estados en los que puede estar la tarea
            filterTask.stateProperty().addListener(((observableValue, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("The filter has finished");
                    alert.show();
                }
            }));

            //Una vez que la tare finaliza de manera exitosa recogemos ese BufferedImage (la imagen) que nos devuelve FilterTask
            filterTask.setOnSucceeded(event -> {
                this.outputImage = filterTask.getValue();
                this.sourceImage = this.outputImage;
                this.workingImage = SwingFXUtils.toFXImage(outputImage, null);
                this.targetImageView.setImage(workingImage);
                this.saveButton.setDisable(false);
                this.undoButton.setDisable(false);
            });

            //Se va actualizando nuestra barra de descarga
            filterTask.messageProperty().addListener((observable, oldValue, newValue) -> {
                //Vamos actualizando nuestro label de manera dinamica en función de la progress bar relacionada con la tarea
                lbStatus.setText(newValue);
            });

            //Creamos el thread (hilo) de la descarga
            new Thread(filterTask).start();
        } catch (MalformedURLException murle){
            murle.printStackTrace();
        }

    }

    @FXML
    private void applyFilters(ActionEvent event){
        this.selectedFilters = new ArrayList<String>(
                this.filterListView.getSelectionModel().getSelectedItems());
        applyBatchFilters();
        SQLiteDB.insertHistorial(sourceImageFile.getName(), String.join(", ", selectedFilters));
    }

    @FXML
    public void saveImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        Stage stage = (Stage) this.saveButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        try {
            ImageIO.write(this.outputImage, "png", file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void undoFilters(ActionEvent event){
        InputStream stream;
        try {
            stream = new FileInputStream(this.sourceImageFile.getAbsolutePath());
            this.sourceImage = ImageIO.read(this.sourceImageFile);
            Image image = new Image(stream);
            this.targetImageView.setImage(image);
            this.saveButton.setDisable(true);
            this.undoButton.setDisable(true);
            this.redoButton.setDisable(false);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void redoFilters(ActionEvent event){
        this.targetImageView.setImage(this.workingImage);
        this.sourceImage = this.outputImage;
        this.saveButton.setDisable(false);
        this.redoButton.setDisable(true);
        this.undoButton.setDisable(false);
    }

}


