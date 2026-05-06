import filtros.FiltrosARGB;
import filtros.Convolucion.FiltrosConvolucionales;
import filtros.HSV.FiltrosBasicosHSV;
import filtros.HSV.FiltrosHSV;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    private JFrame ventana;
    private JLabel etiquetaImagen;
    private int dimX = 1200, dimY = 800;
    private JPanel panelControles;
    private JPanel panelDerecho;
    private JPanel panelListaFiltros;
    private JPanel panelParametros;
    private JTextField campoValor;

    private BufferedImage imagenOriginal;
    private BufferedImage imagenModificada;

    public static void main(String[] args) {
        try {
            // Usamos el tema Darcula de FlatLaf para un look profesional
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarculaLaf());
            // Personalización extra de UI
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception ex) {
            System.err.println("Fallo al inicializar el tema");
        }
        SwingUtilities.invokeLater(() -> new Main().crearInterfaz());
    }

    public void crearInterfaz() {
        ventana = new JFrame("Procesador de Imágenes Pro");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(dimX, dimY);
        ventana.setLayout(new BorderLayout());

        // --- ZONA CENTRAL: Visualizador ---
        etiquetaImagen = new JLabel("Cargue una imagen para comenzar", SwingConstants.CENTER);
        etiquetaImagen.setFont(new Font("SansSerif", Font.BOLD, 16));
        etiquetaImagen.setForeground(Color.GRAY);
        etiquetaImagen.setOpaque(true);
        etiquetaImagen.setBackground(new Color(30, 30, 30));

        JScrollPane panelScroll = new JScrollPane(etiquetaImagen);
        panelScroll.setBorder(null);
        ventana.add(panelScroll, BorderLayout.CENTER);

        // --- ZONA DERECHA: Panel de Herramientas ---
        panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(280, 0));
        panelDerecho.setBackground(new Color(45, 45, 45));
        panelDerecho.setVisible(false);

        // Lista de Filtros
        panelListaFiltros = new JPanel();
        panelListaFiltros.setLayout(new BoxLayout(panelListaFiltros, BoxLayout.Y_AXIS));
        panelListaFiltros.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollFiltros = new JScrollPane(panelListaFiltros);
        scrollFiltros.setBorder(BorderFactory.createTitledBorder("CATEGORÍAS"));
        panelDerecho.add(scrollFiltros, BorderLayout.CENTER);

        // Parámetros
        panelParametros = new JPanel(new GridLayout(2, 1, 5, 5));
        panelParametros.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder("AJUSTES")
        ));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Intensidad:"));
        campoValor = new JTextField("25", 8);
        inputPanel.add(campoValor);

        panelParametros.add(inputPanel);
        panelDerecho.add(panelParametros, BorderLayout.SOUTH);

        ventana.add(panelDerecho, BorderLayout.EAST);

        // --- ZONA INFERIOR: Barra de Acciones ---
        panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelControles.setBackground(new Color(35, 35, 35));

        JButton btnCargar = createStyledButton("Cargar Imagen", new Color(70, 130, 180));
        btnCargar.addActionListener(e -> cargarImagenBase());

        JButton btnFiltros = createStyledButton("Filtros", new Color(60, 179, 113));
        btnFiltros.addActionListener(e -> {
            panelDerecho.setVisible(!panelDerecho.isVisible());
            ventana.revalidate();
        });

        JButton btnRestaurar = createStyledButton("Restaurar Original", new Color(205, 92, 92));
        btnRestaurar.addActionListener(e -> {
            if (imagenOriginal != null) {
                imagenModificada = copiarImagen(imagenOriginal);
                actualizarVista(imagenModificada);
            }
        });

        JButton btnGuardar = createStyledButton("Guardar", new Color(100, 100, 100));
        btnGuardar.addActionListener(e -> guardarImagen());

        panelControles.add(btnCargar);
        panelControles.add(btnFiltros);
        panelControles.add(btnRestaurar);
        panelControles.add(btnGuardar);
        ventana.add(panelControles, BorderLayout.SOUTH);

        configurarCategorias();

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private void configurarCategorias() {
        agregarCategoria("Filtros ARGB", new String[]{"Gris", "Negativo", "Brillo", "Vidrio Esmerilado", "Desvanecimiento Circular", "Efecto Retro", "Blanco y Negro"});
        agregarCategoria("Filtros HSV", new String[]{"Mas saturacion", "Menos saturacion", "Mas brillo", "Menos brillo", "Tonalidad"});
        agregarCategoria("Convolucionales", new String[]{"Blur", "Sharpen o Enfoque", "Detección de Bordes", "Aclarar", "Obscurecer", "Relieve", "Realzar Bordes"});
    }

    private int getValorInt() {
        try {
            return Integer.parseInt(campoValor.getText().trim());
        } catch (NumberFormatException e) {
            return 25;
        }
    }

    // --- CORRECCIÓN CLAVE PARA JPG ---
    private void cargarImagenBase() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));

        if (selector.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage imgCargada = ImageIO.read(selector.getSelectedFile());

                // Forzamos la imagen a TYPE_INT_ARGB para que sea compatible con todos los filtros
                imagenOriginal = new BufferedImage(imgCargada.getWidth(), imgCargada.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = imagenOriginal.createGraphics();
                g.drawImage(imgCargada, 0, 0, null);
                g.dispose();

                imagenModificada = copiarImagen(imagenOriginal);
                actualizarVista(imagenModificada);
                panelDerecho.setVisible(true);
                ventana.revalidate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventana, "Error al cargar JPG/PNG: " + ex.getMessage());
            }
        }
    }

    // Auxiliar para no modificar la original por referencia
    private BufferedImage copiarImagen(BufferedImage bi) {
        BufferedImage b = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics g = b.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return b;
    }

    private void agregarCategoria(String nombre, String[] filtros) {
        JButton btnCat = new JButton(nombre + " ▼");
        btnCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnCat.setHorizontalAlignment(SwingConstants.LEFT);
        btnCat.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setVisible(false);

        for (String f : filtros) {
            JButton btnF = new JButton("  " + f);
            btnF.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            btnF.setHorizontalAlignment(SwingConstants.LEFT);
            btnF.setContentAreaFilled(false);

            btnF.addActionListener(e -> aplicarFiltro(f));
            subPanel.add(btnF);
        }

        btnCat.addActionListener(e -> subPanel.setVisible(!subPanel.isVisible()));
        panelListaFiltros.add(btnCat);
        panelListaFiltros.add(subPanel);
    }

    private void aplicarFiltro(String f) {
        if (imagenModificada == null) return;

        int val = getValorInt();

        switch (f) {
            case "Gris": imagenModificada = FiltrosARGB.filtroGris(imagenModificada); break;
            case "Negativo": imagenModificada = FiltrosARGB.filtroNegativo(imagenModificada); break;
            case "Brillo": imagenModificada = FiltrosARGB.filtroBrillo(imagenModificada, val); break;
            case "Vidrio Esmerilado": imagenModificada = FiltrosARGB.filtroVidrioEsmerilado(imagenModificada); break;
            case "Efecto Retro": imagenModificada = FiltrosARGB.filtroEfectoRetro(imagenModificada, val); break;
            case "Desvanecimiento Circular": imagenModificada = FiltrosARGB.filtroDesvanecimientoCircular(imagenModificada); break;
            case "Blanco y Negro": imagenModificada = FiltrosARGB.filtroBlancoNegro(imagenModificada); break;
            case "Mas saturacion": imagenModificada = FiltrosBasicosHSV.masSaturacion(imagenModificada); break;
            case "Menos saturacion": imagenModificada = FiltrosBasicosHSV.menosSaturacion(imagenModificada); break;
            case "Mas brillo": imagenModificada = FiltrosBasicosHSV.masBrillo(imagenModificada); break;
            case "Menos brillo": imagenModificada = FiltrosBasicosHSV.menosBrillo(imagenModificada); break;
            case "Tonalidad": imagenModificada = FiltrosBasicosHSV.cambioTonalidad(imagenModificada); break;
            case "Blur": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Blur", imagenModificada); break;
            case "Sharpen o Enfoque": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Sharpen", imagenModificada); break;
            case "Detección de Bordes": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("DeteccionBordes", imagenModificada); break;
            case "Aclarar": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Aclarar", imagenModificada); break;
            case "Obscurecer": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Obscurecer", imagenModificada); break;
            case "Relieve": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Relieve", imagenModificada); break;
            case "Realzar Bordes": imagenModificada = FiltrosConvolucionales.filtroConvoluciones("Realzar Bordes", imagenModificada); break;
        }
        actualizarVista(imagenModificada);
    }

    private void guardarImagen() {
        if (imagenModificada == null) {
            JOptionPane.showMessageDialog(ventana, "No hay imagen para guardar.");
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar Imagen");

        // Filtros para que el usuario elija el formato en la ventana
        FileNameExtensionFilter filtroPng = new FileNameExtensionFilter("Imagen PNG (.png)", "png");
        FileNameExtensionFilter filtroJpg = new FileNameExtensionFilter("Imagen JPG (.jpg)", "jpg");
        selector.addChoosableFileFilter(filtroPng);
        selector.addChoosableFileFilter(filtroJpg);
        selector.setFileFilter(filtroPng); // PNG por defecto

        if (selector.showSaveDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivoDestino = selector.getSelectedFile();
                String ruta = archivoDestino.getAbsolutePath().toLowerCase();

                // Determinar qué formato eligió el usuario en el combo box
                String formato = "png";
                if (selector.getFileFilter() == filtroJpg) {
                    formato = "jpg";
                }

                // Asegurar que el nombre del archivo tenga la extensión correcta
                if (!ruta.endsWith("." + formato)) {
                    archivoDestino = new File(archivoDestino.getAbsolutePath() + "." + formato);
                }

                if (formato.equals("jpg")) {
                    // PASO CRÍTICO: Para JPG debemos remover la transparencia (ARGB -> RGB)
                    BufferedImage jpgImage = new BufferedImage(
                            imagenModificada.getWidth(),
                            imagenModificada.getHeight(),
                            BufferedImage.TYPE_INT_RGB
                    );
                    Graphics2D g2d = jpgImage.createGraphics();
                    // Dibujamos un fondo blanco por si la imagen tenía partes transparentes
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0, 0, jpgImage.getWidth(), jpgImage.getHeight());
                    g2d.drawImage(imagenModificada, 0, 0, null);
                    g2d.dispose();

                    ImageIO.write(jpgImage, "jpg", archivoDestino);
                } else {
                    // Para PNG guardamos directo
                    ImageIO.write(imagenModificada, "png", archivoDestino);
                }

                JOptionPane.showMessageDialog(ventana, "¡Imagen guardada como " + formato.toUpperCase() + "!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(ventana, "Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void actualizarVista(BufferedImage img) {
        if (img != null) {
            etiquetaImagen.setText("");
            int anchoOcupado = panelDerecho.isVisible() ? panelDerecho.getWidth() : 0;
            int anchoCont = ventana.getWidth() - anchoOcupado - 40;
            int altoCont = ventana.getHeight() - panelControles.getHeight() - 80;

            double ratio = Math.min((double) anchoCont / img.getWidth(), (double) altoCont / img.getHeight());
            int nW = (int) (img.getWidth() * ratio);
            int nH = (int) (img.getHeight() * ratio);

            Image escalada = img.getScaledInstance(nW, nH, Image.SCALE_SMOOTH);
            etiquetaImagen.setIcon(new ImageIcon(escalada));
        }
    }
}