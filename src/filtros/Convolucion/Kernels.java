package filtros.Convolucion;

public class Kernels {

    //Nota: Se debe normalizar cuando es diferente de 0 o 1
    //Enfoque (Sharpen)
    public static final float[] kEnfoque = {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
    };

    //Desenfoque (blur)
    public static final float[] kDesenfoque = {
            1f/9, 1f/9, 1f/9,
            1f/9, 1f/9, 1f/9,
            1f/9, 1f/9, 1f/9
    };

    //Deteccion de bordes
    public static final float[] kBordes = {
            -1f, -1f, -1f,
            -1f, 8f, -1f,
            -1f, -1f, -1f
    };


    //Aclarar
    public static final float[] kAclaracion = {
            0.1f, 0.1f, 0.1f,
            0.1f, 1f, 0.1f,
            0.1f, 0.1f, 0.1f
    };

    //Obscurecer
    public static final float[] kObscurecer = {
            0.01f, 0.01f, 0.01f,
            0.01f, 0.5f, 0.01f,
            0.01f, 0.01f, 0.01f
    };

    //Efecto de Relieve
    public static final float[] kRelieve = {
            -2f, -1f, 0f,
            -1f,  1f, 1f,
            0f,  1f, 2f
    };

    //Realzar Bordes
    public static final float[] kReBordes = {
            0f, 0f, 0f,
            -1f,  1f, 0f,
            0f,  0f, 0f
    };
}
