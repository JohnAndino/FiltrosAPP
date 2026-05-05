import filtros.FiltrosARGB;
import filtros.Convolucion.FiltrosConvolucionales;
import filtros.HSV.FiltrosHSV;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    // Variables globales para la interfaz
    private JFrame ventana;
    private JLabel etiquetaImagen;
    int dimX = 1100, dimY = 700; // Aumentamos un poco el ancho para el panel lateral
    int anchoCont = dimX;
    int altoCont = dimY;
    JPanel panelControles;

    // --- NUEVAS VARIABLES PARA EL PANEL LATERAL ---
    private JPanel panelDerecho;
    private JPanel panelListaFiltros;
    private JPanel panelParametros;

    // Variables vitales para los filtros en tiempo real
    private BufferedImage imagenOriginal;
    private BufferedImage imagenModificada;
    private BufferedImage imgkevin;

    //Instancias
    FiltrosARGB fil = new FiltrosARGB();

    public static void main(String[] args) {
        try {
            // Aquí eliges el sabor: FlatDarkLaf (Oscuro) o FlatLightLaf (Claro)
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Fallo al inicializar el tema");
        }
        SwingUtilities.invokeLater(() -> {
            new Main().crearInterfaz();
        });
    }

    public void crearInterfaz() {
        ventana = new JFrame("Mi Editor de Imágenes");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(dimX, dimY);
        ventana.setLayout(new BorderLayout());

        // 1. ZONA CENTRAL: Imagen
        etiquetaImagen = new JLabel("Haz clic en 'Cargar Imagen' para empezar", SwingConstants.CENTER);
        etiquetaImagen.setOpaque(true);
        etiquetaImagen.setBackground(Color.LIGHT_GRAY);
        etiquetaImagen.setHorizontalAlignment(JLabel.CENTER);
        etiquetaImagen.setVerticalAlignment(JLabel.CENTER);

        JScrollPane panelScroll = new JScrollPane(etiquetaImagen);
        ventana.add(panelScroll, BorderLayout.CENTER);

        // 2. ZONA DERECHA: Menú de Filtros y Parámetros (Inicialmente oculto)
        panelDerecho = new JPanel();
        panelDerecho.setLayout(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(250, 0));
        panelDerecho.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));
        panelDerecho.setVisible(false); // Oculto hasta presionar "Filtros"

        // Sub-panel superior: Lista de filtros con Scroll
        panelListaFiltros = new JPanel();
        panelListaFiltros.setLayout(new BoxLayout(panelListaFiltros, BoxLayout.Y_AXIS));
        panelListaFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollFiltros = new JScrollPane(panelListaFiltros);
        scrollFiltros.setBorder(BorderFactory.createTitledBorder("Categorías y Filtros"));

        // Sub-panel inferior: Parámetros
        panelParametros = new JPanel();
        panelParametros.setLayout(new FlowLayout());
        panelParametros.setPreferredSize(new Dimension(250, 150));
        panelParametros.setBorder(BorderFactory.createTitledBorder("Parámetros"));

        panelDerecho.add(scrollFiltros, BorderLayout.CENTER);
        panelDerecho.add(panelParametros, BorderLayout.SOUTH);
        ventana.add(panelDerecho, BorderLayout.EAST);

        // 3. ZONA INFERIOR: Controles Principales
        panelControles = new JPanel();
        panelControles.setLayout(new FlowLayout());

        JButton btnCargar = new JButton("Cargar");
        btnCargar.addActionListener(e -> cargarImagenBase());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarImagen());

        JButton btnMenuFiltros = new JButton("Filtros");
        btnMenuFiltros.addActionListener(e -> {
            panelDerecho.setVisible(!panelDerecho.isVisible());
            ventana.revalidate(); // Refresca la interfaz para mostrar/ocultar el panel
        });

        JButton btnRestaurar = new JButton("Imagen Original");
        btnRestaurar.addActionListener(e -> {
            if (imagenOriginal != null) {
                imagenModificada = imagenOriginal;
                actualizarVista(imagenModificada);
            } else {
                JOptionPane.showMessageDialog(ventana, "No hay ninguna imagen cargada para restaurar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panelControles.add(btnCargar);
        panelControles.add(btnGuardar);
        panelControles.add(btnMenuFiltros);
        panelControles.add(btnRestaurar);
        ventana.add(panelControles, BorderLayout.SOUTH);

        // Categorias
        agregarCategoria("Filtros ARGB", new String[]{"Gris", "Negativo", "Brillo", "Vidrio Esmerilado", "Desvanecimiento Circular", "Efecto Retro", "Blanco y Negro"});
        agregarCategoria("Filtros HSV", new String[]{"Calido", "Frio", "Pastel", "Vintage", "Neon"});
        agregarCategoria("Convolucionales", new String[]{"Blur", "Sharpen o Enfoque", "Detección de Bordes", "Aclarar", "Obscurecer", "Relieve", "Realzar Bordes"});

        ventana.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (imagenModificada != null) {
                    actualizarVista(imagenModificada);
                }
            }
        });

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    // Metodo auxiliar para crear las categorías colapsables o secciones
    private void agregarCategoria(String nombre, String[] filtros) {
        // Botón de la Categoría
        JButton btnCategoria = new JButton("▼ " + nombre);
        btnCategoria.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
        btnCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // que ocupe todo el ancho
        btnCategoria.setBorderPainted(false); // Opcional: para un look más limpio
        btnCategoria.setContentAreaFilled(false);
        btnCategoria.setHorizontalAlignment(SwingConstants.LEFT); // Texto del botón a la izquierda

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Panel de filtros a la izquierda
        subPanel.setVisible(true);

        for (String f : filtros) {
            JButton btnF = new JButton("   • " + f); // Espacio extra para simular jerarquía
            btnF.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
            btnF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            btnF.setBorderPainted(false);
            btnF.setContentAreaFilled(false);
            btnF.setHorizontalAlignment(SwingConstants.LEFT); // Texto a la izquierda

            //Funcionalidad de los botones en las categorias
            if (f.equals("Gris")) {
                btnF.addActionListener(e -> filtroGris());
            } else if (f.equals("Brillo")) {
                btnF.addActionListener(e -> filtroBrillo());
            } else if (f.equals("Negativo")) {
                btnF.addActionListener(e -> filtroNegativo());
            } else if (f.equals("Vidrio Esmerilado")) {
                btnF.addActionListener(e -> filtroVidrioEsmerilado());
            } else if (f.equals("Desvanecimiento Circular")) {
                btnF.addActionListener(e -> filtroDesvanecimientoCircular());
            } else if (f.equals("Efecto Retro")) {
                btnF.addActionListener(e -> filtroEfectoRetro());
            } else if (f.equals("Blanco y Negro")) {
                btnF.addActionListener(e -> filtroBlancoNegro());
            } else if (f.equals("Calido")) {
                btnF.addActionListener(e -> filtroCalidoHsv());
            } else if (f.equals("Frio")) {
                btnF.addActionListener(e -> filtroFrioHsv());
            } else if (f.equals("Pastel")) {
                btnF.addActionListener(e -> filtroPastelHsv());
            }
            // metodos Convolusionales
            else if (f.equals("Blur")) {
                btnF.addActionListener(e -> filtroBlurCv());
            } else if (f.equals("Sharpen o Enfoque")) {
                btnF.addActionListener(e -> filtroSharpenCv());
            } else if (f.equals("Detección de Bordes")) {
                btnF.addActionListener(e -> filtroDeteccionBordesCv());
            } else if (f.equals("Aclarar")) {
                btnF.addActionListener(e -> filtroAclararCv());
            } else if (f.equals("Obscurecer")) {
                btnF.addActionListener(e -> filtroObscurecerCv());
            } else if (f.equals("Relieve")) {
                btnF.addActionListener(e -> filtroRelieveCv());
            } else if (f.equals("Realzar Bordes")) {
                btnF.addActionListener(e -> filtroRealzarBordesCv());
            }

            subPanel.add(btnF);
        }

        btnCategoria.addActionListener(e -> subPanel.setVisible(!subPanel.isVisible()));

        panelListaFiltros.add(btnCategoria);
        panelListaFiltros.add(subPanel);
    }

    // --- MÉTODOS PREVIOS (SIN CAMBIOS) ---

    private void cargarImagenBase() {
        JFileChooser selectorArchivos = new JFileChooser();
        selectorArchivos.setDialogTitle("Selecciona una imagen");
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Imágenes (PNG, JPG)", "png", "jpg", "jpeg");
        selectorArchivos.setFileFilter(filtro);

        int resultado = selectorArchivos.showOpenDialog(ventana);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = selectorArchivos.getSelectedFile();
            try {
                imagenOriginal = ImageIO.read(archivo);
                imagenModificada = imagenOriginal;
                actualizarVista(imagenModificada);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, "Error al leer la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarImagen() {
        if (imagenModificada == null) {
            JOptionPane.showMessageDialog(ventana, "No hay ninguna imagen para guardar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar Imagen");

        // Filtros de extensión
        FileNameExtensionFilter filtroPng = new FileNameExtensionFilter("Imagen PNG (.png)", "png");
        FileNameExtensionFilter filtroJpg = new FileNameExtensionFilter("Imagen JPG (.jpg)", "jpg");
        selector.addChoosableFileFilter(filtroPng);
        selector.addChoosableFileFilter(filtroJpg);
        selector.setFileFilter(filtroPng); // PNG por defecto

        int seleccion = selector.showSaveDialog(ventana);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivoDestino = selector.getSelectedFile();
            String ruta = archivoDestino.getAbsolutePath();

            // Obtener la extensión seleccionada por el usuario en el combo box
            String extension = "png"; // valor por defecto
            if (selector.getFileFilter() == filtroJpg) extension = "jpg";

            // Asegurarnos de que el archivo tenga la extensión correcta en el nombre
            if (!ruta.toLowerCase().endsWith("." + extension)) {
                archivoDestino = new File(ruta + "." + extension);
            }

            try {
                if (extension.equals("jpg")) {
                    // Si es JPG, creamos una versión sin transparencia (RGB)
                    BufferedImage imagenRGB = new BufferedImage(
                            imagenModificada.getWidth(),
                            imagenModificada.getHeight(),
                            BufferedImage.TYPE_INT_RGB
                    );
                    Graphics g = imagenRGB.getGraphics();
                    g.drawImage(imagenModificada, 0, 0, null);
                    g.dispose();
                    ImageIO.write(imagenRGB, "jpg", archivoDestino);
                } else {
                    // Si es PNG, guardamos directamente el buffer modificado
                    ImageIO.write(imagenModificada, "png", archivoDestino);
                }
                JOptionPane.showMessageDialog(ventana, "Imagen guardada con éxito");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, "Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void filtroGris() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroGris(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroNegativo() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroNegativo(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroBrillo() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroBrillo(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroVidrioEsmerilado() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroVidrioEsmerilado(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroDesvanecimientoCircular() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroDesvanecimientoCircular(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroEfectoRetro() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroEfectoRetro(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroBlancoNegro() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosARGB.filtroBlancoNegro(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroCalidoHsv() {
        if (imagenModificada != null) {
            // imagenModificada = fil.filtroGris(imagenModificada);
            imagenModificada = FiltrosHSV.filtroCalido(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroFrioHsv() {
        if (imagenModificada != null) {
            // imagenModificada = fil.filtroGris(imagenModificada);
            imagenModificada = FiltrosHSV.filtroFrio(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroPastelHsv() {
        if (imagenModificada != null) {
            // imagenModificada = fil.filtroGris(imagenModificada);
            imagenModificada = FiltrosHSV.filtroPastel(imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroBlurCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Blur", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroSharpenCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Sharpen", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroDeteccionBordesCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("DeteccionBordes", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroAclararCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Aclarar", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroObscurecerCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Obscurecer", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroRelieveCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Relieve", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }

    private void filtroRealzarBordesCv() {
        if (imagenModificada != null) {
            imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Realzar Bordes", imagenModificada);
            actualizarVista(imagenModificada);
        }
    }


    private void actualizarVista(BufferedImage img) {
        if (img != null) {
            etiquetaImagen.setText("");
            // Restamos el ancho del panel derecho si es visible para que la imagen no se recorte
            int anchoOcupado = panelDerecho.isVisible() ? panelDerecho.getWidth() : 0;
            int anchoCont = ventana.getContentPane().getWidth() - anchoOcupado;
            int altoCont = ventana.getContentPane().getHeight() - panelControles.getHeight();

            int nuevoAlto, nuevoAncho;
            double arImg = (double) img.getWidth() / img.getHeight();
            double arCont = (double) anchoCont / altoCont;

            if (arImg > arCont) {
                nuevoAncho = anchoCont;
                nuevoAlto = (int) (anchoCont / arImg);
            } else {
                nuevoAlto = altoCont;
                nuevoAncho = (int) (altoCont * arImg);
            }

            Image imgEscalada = img.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
            etiquetaImagen.setIcon(new ImageIcon(imgEscalada));
            ventana.revalidate();
            ventana.repaint();
        }
    }
}