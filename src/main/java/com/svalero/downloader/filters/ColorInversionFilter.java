package com.svalero.downloader.filters;

import java.awt.*;

public class ColorInversionFilter {

    public static Color apply(Color sourceColor) {

        int red = sourceColor.getRed();
        int green = sourceColor.getGreen();
        int blue = sourceColor.getBlue();

        //Invertimos los componentes de cada color
        int invertedRed = 255 - red;
        int invertedGreen = 255 - green;
        int invertedBlue = 255 - blue;

        return new Color(invertedRed, invertedGreen, invertedBlue);

    }
}
