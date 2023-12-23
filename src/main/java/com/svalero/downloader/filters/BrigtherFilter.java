package com.svalero.downloader.filters;

import java.awt.Color;

public class BrigtherFilter {

    public static Color apply(Color sourceColor){
       //Contstante de cuanto vamos a aumentar el brillo
        int brightnessFactor = 50;
        int red = sourceColor.getRed();
        int green = sourceColor.getGreen();
        int blue = sourceColor.getBlue();

        //Para aumentar el brillo sumamos esa constante a los colores, si la suma es <255 nos quedamos con ella pero sino nos quedamos con 255 ya que no se puede superar
        red = Math.min(255, red + brightnessFactor);
        green = Math.min(255, green + brightnessFactor);
        blue = Math.min(255, blue + brightnessFactor);

        //Creamos el nuevo color (filtro)
        return new Color(red, green, blue);

    }
}
