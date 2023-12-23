package com.svalero.downloader.filters;

import java.awt.Color;

public class GrayscaleFilter {

    public static Color apply(Color sourceColor){
        int red = sourceColor.getRed();
        int green = sourceColor.getGreen();
        int blue = sourceColor.getBlue();

        //Creamos el color gris con la media de los 3 colores
        int gray = (red + green + blue) / 3;
        //Creamos el nuevo objeto (color) que tendra el filtro de la imagen
        Color newColor = new Color(gray, gray, gray);
        //Devolvemos el nuevo color
        return newColor;
    }
}
