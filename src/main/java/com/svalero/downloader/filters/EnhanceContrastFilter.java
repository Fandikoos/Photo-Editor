package com.svalero.downloader.filters;

import java.awt.*;

public class EnhanceContrastFilter {

    public static Color apply(Color sourceColor){

        int red = sourceColor.getRed();
        int green = sourceColor.getGreen();
        int blue = sourceColor.getBlue();

        //Calculamos el valor medio de los 3 colores
        int meanValue = (red + green + blue) / 3;

        //Ajustamos cada componente de color para mejorar su propio contraste
        int contrastRed = 2 * (red - meanValue) + meanValue;
        int contrastGreen = 2 * (green - meanValue) + meanValue;
        int contrastBlue = 2 * (blue - meanValue) + meanValue;

        //Nos aseguramos de que los valores esten en el rango permitido
        contrastRed = Math.min(255, Math.max(0, contrastRed));
        contrastGreen = Math.min(255, Math.max(0, contrastGreen));
        contrastBlue = Math.min(255, Math.max(0, contrastBlue));

        return new Color(contrastRed, contrastGreen, contrastBlue);
    }
}
