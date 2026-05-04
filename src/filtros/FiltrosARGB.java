package filtros;

import java.awt.image.BufferedImage;

public class FiltrosARGB {

    public FiltrosARGB(){
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
