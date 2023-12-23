package com.svalero.downloader.filters;

import java.awt.*;

public class SepiaFilter {

    public static Color apply(Color sourceColor){
        int sepiaIntensity = 30;
        int red = sourceColor.getRed();
        int green = sourceColor.getGreen();
        int blue = sourceColor.getBlue();

        int newRed = (int) (0.393 * red + 0.769 * green + 0.189 * blue);
        int newGreen = (int) (0.349 * red + 0.686 * green + 0.168 * blue);
        int newBlue = (int) (0.272 * red + 0.534 * green + 0.131 * blue);

        newRed = Math.min(255, newRed + sepiaIntensity);
        newGreen = Math.min(255, newGreen + sepiaIntensity);
        newBlue = Math.min(255, newBlue + sepiaIntensity);

        return new Color(newRed, newGreen, newBlue);
    }
}
