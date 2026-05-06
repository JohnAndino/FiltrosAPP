package filtros.HSV;
import java.awt.image.BufferedImage;
import java.awt.Color;
public class FiltrosBasicosHSV {

    //   mas saturacion
    public static BufferedImage masSaturacion(BufferedImage img) {
        return procesar(img, (hsb) -> {
            hsb[1] = Math.min(1f, hsb[1] * 1.5f);
            return hsb;
        });
    }

    //   menos saturacion
    public static BufferedImage menosSaturacion(BufferedImage img) {
        return procesar(img, (hsb) -> {
            hsb[1] = hsb[1] * 0.5f;
            return hsb;
        });
    }

    //   mass brillo
    public static BufferedImage masBrillo(BufferedImage img) {
        return procesar(img, (hsb) -> {
            hsb[2] = Math.min(1f, hsb[2] * 1.3f);
            return hsb;
        });
    }

    //   menos brillo
    public static BufferedImage menosBrillo(BufferedImage img) {
        return procesar(img, (hsb) -> {
            hsb[2] = hsb[2] * 0.6f;
            return hsb;
        });
    }

    //   cambio de tonalidad (Hue shift)
    public static BufferedImage cambioTonalidad(BufferedImage img) {
        return procesar(img, (hsb) -> {
            hsb[0] = (hsb[0] + 0.2f) % 1f; // cambia el color
            return hsb;
        });
    }

    //  metodo base reutilizable
    public static BufferedImage procesar(BufferedImage img, ModificadorHSB mod) {

        int ancho = img.getWidth();
        int alto = img.getHeight();

        BufferedImage out = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                int pixel = img.getRGB(x, y);

                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);

                hsb = mod.modificar(hsb);

                int nuevoRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

                int r2 = (nuevoRGB >> 16) & 0xFF;
                int g2 = (nuevoRGB >> 8) & 0xFF;
                int b2 = nuevoRGB & 0xFF;

                int nuevoPixel = (a << 24) | (r2 << 16) | (g2 << 8) | b2;

                out.setRGB(x, y, nuevoPixel);
            }
        }

        return out;
    }

    // interfaz funcional
    interface ModificadorHSB {
        float[] modificar(float[] hsb);
    }

}
