package com.svalero.downloader.task;

import com.svalero.downloader.filters.*;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class FilterTask extends Task<BufferedImage> {

    private File sourceImage;
    private List<String> selectedFilters;

    public FilterTask(File sourceImage, List<String> selectedFilters) throws MalformedURLException {
        this.sourceImage = sourceImage;
        this.selectedFilters = selectedFilters;
    }

    //Este método nos devolvera la imagen
    @Override
    protected BufferedImage call() throws Exception {
        int totalProcessedPixels = 0;
        updateMessage("Starting Filter");
        //Convertimos el fichero del principio en una imagen gracias a BufferedImage
        BufferedImage image = ImageIO.read(this.sourceImage);
        //Calculamos el tamaño de la imagen (altura x anchura)
        int imageSize = image.getHeight() * image.getWidth();
        float totalProcessed = 0f;

        for (int y = 0; y < image.getHeight(); y++){
            Thread.sleep(10);
            for (int x = 0; x < image.getWidth(); x++){
                Color color = new Color(image.getRGB(x, y));
                for (String selectedFilter : this.selectedFilters){
                    if (selectedFilter.equals("Grayscale"))
                        color = GrayscaleFilter.apply(color);
                    if (selectedFilter.equals("Brighter"))
                        color = BrigtherFilter.apply(color);
                    if (selectedFilter.equals("Sepia"))
                        color = SepiaFilter.apply(color);
                    if (selectedFilter.equals("Color Inversion"))
                        color = ColorInversionFilter.apply(color);
                    if (selectedFilter.equals("Enhance Contrast"))
                        color = EnhanceContrastFilter.apply(color);
                }
                if (color != null)
                    image.setRGB(x, y, color.getRGB());

                    totalProcessedPixels++;
                updateProgress(totalProcessedPixels, imageSize);
                totalProcessed = totalProcessedPixels / (float)imageSize;
                String totalProcessedFormatted = String.format("%.2f", 100 * totalProcessed);
                updateMessage(totalProcessedFormatted + " %");
            }
        }
        updateProgress(totalProcessedPixels, imageSize);
        updateMessage("100 %");
        return image;
    }
}
