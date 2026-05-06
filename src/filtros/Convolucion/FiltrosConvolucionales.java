package filtros.Convolucion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class FiltrosConvolucionales {

    static BufferedImage buffer1;
    static float[] matriz;

    public FiltrosConvolucionales() {
    }

    public static BufferedImage filtroConvoluciones(String nombre, BufferedImage img) {

        try {
            buffer1 = img;

            switch (nombre){
                case "Blur":
                    matriz = Kernels.kDesenfoque;
                    for (int i = 0; i < 5; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                case "Sharpen":
                    matriz = Kernels.kEnfoque;
                    for (int i = 0; i < 1; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                case "DeteccionBordes":
                    matriz = Kernels.kBordes;
                    for (int i = 0; i < 1; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                case "Aclarar":
                    matriz = Kernels.kAclaracion;
                    for (int i = 0; i < 1; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                case "Obscurecer":
                    matriz = Kernels.kObscurecer;
                    for (int i = 0; i < 1; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                case "Relieve":
                    matriz = Kernels.kRelieve;
                    for (int i = 0; i < 1; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                case "Realzar Bordes":
                    matriz = Kernels.kReBordes;
                    for (int i = 0; i < 1; i++) {
                        buffer1 = convoluciones(buffer1);
                    }
                    break;
                default:
                    System.out.println("Debe elegir un efecto existente");
                    break;
            }

            return buffer1;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

//    public static BufferedImage convulsiones(BufferedImage img) {
//        int ancho = img.getWidth();
//        int alto = img.getHeight();
//
//        int[] pixelesOriginales = img.getRGB(0, 0, ancho, alto, null, 0, ancho);
//        int[] pixelesNuevos = new int[pixelesOriginales.length];
//
//        for (int y = 1; y < alto - 1; y++) {
//            for (int x = 1; x < ancho - 1; x++) {
//                float sumR = 0, sumG = 0, sumB = 0;
//                int a = (pixelesOriginales[y * ancho + x] >> 24) & 0xFF;
//
//                for (int i = -1; i <= 1; i++) {
//                    for (int j = -1; j <= 1; j++) {
//                        int pixel = pixelesOriginales[(y + j) * ancho + (x + i)];
//                        float valMatriz = matrizConvolusion[i + 1][j + 1];
//
//                        sumR += ((pixel >> 16) & 0xFF) * valMatriz;
//                        sumG += ((pixel >> 8) & 0xFF) * valMatriz;
//                        sumB += (pixel & 0xFF) * valMatriz;
//                    }
//                }
//
//                int r = Math.min(255, Math.max(0, (int) sumR));
//                int g = Math.min(255, Math.max(0, (int) sumG));
//                int b = Math.min(255, Math.max(0, (int) sumB));
//
//                pixelesNuevos[y * ancho + x] = (a << 24) | (r << 16) | (g << 8) | b;
//            }
//        }
//
//        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
//        salida.setRGB(0, 0, ancho, alto, pixelesNuevos, 0, ancho);
//        return salida;
//    }

    public static BufferedImage convoluciones(BufferedImage img) {
        Kernel kernel = new Kernel(
                (int)Math.sqrt(matriz.length),
                (int)Math.sqrt(matriz.length),
                matriz);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage imgRGB = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = imgRGB.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        // Aplicamos el filtro sobre la imagen sin canal alfa por eso la transformacion a RGB
        BufferedImage buffer2 = op.filter(imgRGB, null);

        return buffer2;
    }

}
