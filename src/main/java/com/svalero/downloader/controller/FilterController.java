package com.svalero.downloader.controller;

import com.svalero.downloader.task.FilterTask;
import com.svalero.downloader.task.HistorialTask;
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

    private File sourceImage;
    private List<String> selectedFilters;
    private FilterTask filterTask;
    private BufferedImage outputImage;
    private List<HistorialTask> historial = new ArrayList<>();

    //Constructor de nuestra clase y en el que recogeremos la imagen seleccionada y el filtro
    public FilterController(File sourceImage, List<String> selectedFilters){
        this.sourceImage = sourceImage;
        this.selectedFilters = selectedFilters;
        this.outputImage = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStream stream;
        try {
            //Cargamos la imagen que hemos seleccionado en la interfaz gráfica
            stream = new FileInputStream(sourceImage.getAbsolutePath());
            //Creamos la imagen
            Image image = new Image(stream);
            //La conectamos a la interfaz gráfica
            this.sourceImageView.setImage(image);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
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
                Image image = SwingFXUtils.toFXImage(outputImage, null);
                this.targetImageView.setImage(image);
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

}


