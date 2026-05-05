package filtros;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class FiltrosARGB {

    public FiltrosARGB() {
    }

    public static BufferedImage filtroBrillo(BufferedImage img) {
        int ancho, alto, pixel, pixelNuevo;
        int r = 0, g = 0, b = 0, a = 0;
        int mascara = 0xFF;
        int brillo = 100;

        try {

            ancho = img.getWidth();
            alto = img.getHeight();

            BufferedImage buffer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    pixel = img.getRGB(x, y);

                    a = (pixel >> 24) & mascara;
                    r = (pixel >> 16) & mascara;
                    g = (pixel >> 8) & mascara;
                    b = (pixel >> 0) & mascara;

                    r = Math.min(255, (r + brillo));
                    g = Math.min(255, (g + brillo));
                    b = Math.min(255, (b + brillo));

                    pixelNuevo = (a << 24) | (r << 16) | (g << 8) | (b << 0);

                    buffer.setRGB(x, y, pixelNuevo);

                }
            }

            return buffer;

        } catch (Exception e) {
            System.out.println("No se pudo aplicar el filtro para el Brillo");
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage filtroNegativo(BufferedImage img) {
        int ancho, alto, pixel, pixelNuevo;
        int r = 0, g = 0, b = 0, a = 0;
        int mascara = 0xFF;

        try {

            ancho = img.getWidth();
            alto = img.getHeight();

            BufferedImage buffer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    pixel = img.getRGB(x, y);

                    a = (pixel >> 24) & mascara;
                    r = (pixel >> 16) & mascara;
                    g = (pixel >> 8) & mascara;
                    b = (pixel >> 0) & mascara;

                    r = (255 - r);
                    g = (255 - g);
                    b = (255 - b);

                    pixelNuevo = (a << 24) | (r << 16) | (g << 8) | (b << 0);

                    buffer.setRGB(x, y, pixelNuevo);

                }
            }
            return buffer;
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el filtro Negativo");
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage filtroGris(BufferedImage img) {
        int a, r, g, b;
        int pixel;
        int mascara = 0xFF;
        int gris;
        int ancho, alto;


        try {
            ancho = img.getWidth();
            alto = img.getHeight();
            BufferedImage bufferAux = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    pixel = img.getRGB(x, y);

                    a = (pixel >> 24) & mascara;
                    r = (pixel >> 16) & mascara;
                    g = (pixel >> 8) & mascara;
                    b = (pixel) & mascara;

                    gris = (int) ((0.2125f * r) + (0.7154f * g) + (0.0721f * b));

                    pixel = (a << 24) | (gris << 16) | (gris << 8) | (gris);
                    bufferAux.setRGB(x, y, pixel);
                }
            }
            return bufferAux;
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el filtro Gris");
            throw new RuntimeException(e);
        }

    }

}
