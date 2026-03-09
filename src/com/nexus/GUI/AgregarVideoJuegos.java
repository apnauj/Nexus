package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoYaExiste;
import com.nexus.exceptions.EValorNegativo;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Pantalla para agregar un producto tipo Videojuego.
 */
public class AgregarVideoJuegos extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCategoria;
    private JTextField txtTiempoGarantia;
    private JTextField txtPrecioBase;
    private JTextField txtStock;
    private JTextField txtDesarrolladores;
    private JTextField txtGeneros;
    private JCheckBox chkMultijugador;
    private JTextField txtFechaLanzamiento;
    private JTextField txtPlataforma;
    private JTextField txtTamano;

    public AgregarVideoJuegos() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Agregar Videojuego");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 540, 620);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarProductos().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 6));

        panelForm.add(crearFila("Nombre: ", txtNombre = crearTextField(28)));
        panelForm.add(crearFila("Descripción: ", txtDescripcion = crearTextField(28)));
        panelForm.add(crearFila("Categoría: ", txtCategoria = crearTextField(22)));
        panelForm.add(crearFila("Tiempo garantía (meses): ", txtTiempoGarantia = crearTextField(6)));
        panelForm.add(crearFila("Precio base: ", txtPrecioBase = crearTextField(12)));
        panelForm.add(crearFila("Stock: ", txtStock = crearTextField(6)));
        panelForm.add(crearFilaDosLineas("Desarrolladores (separados por coma):", txtDesarrolladores = crearTextField(38)));
        panelForm.add(crearFilaDosLineas("Géneros (separados por coma):", txtGeneros = crearTextField(38)));
        JPanel rowMult = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowMult.add(new JLabel("Multijugador: "));
        chkMultijugador = new JCheckBox();
        rowMult.add(chkMultijugador);
        panelForm.add(rowMult);
        panelForm.add(crearFila("Fecha lanzamiento (dd/MM/yyyy): ", txtFechaLanzamiento = crearTextField(12)));
        panelForm.add(crearFila("Plataforma: ", txtPlataforma = crearTextField(15)));
        panelForm.add(crearFila("Tamaño (GB): ", txtTamano = crearTextField(8)));
        panelForm.add(Box.createVerticalStrut(24));

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        scrollForm.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar Videojuego");
        btnAgregar.addActionListener(e -> agregarVideojuego());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearFila(String label, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }

    private JPanel crearFilaDosLineas(String label, JTextField field) {
        JPanel row = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }

    private JTextField crearTextField(int cols) {
        JTextField t = new JTextField(cols);
        t.setForeground(Color.BLACK);
        t.setBackground(Color.WHITE);
        t.setCaretColor(Color.BLACK);
        return t;
    }

    private void agregarVideojuego() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String categoria = txtCategoria.getText();
        String tiempoStr = txtTiempoGarantia.getText();
        String precioStr = txtPrecioBase.getText();
        String stockStr = txtStock.getText();
        String devStr = txtDesarrolladores.getText();
        String genStr = txtGeneros.getText();
        String plataforma = txtPlataforma.getText();
        String fechaStr = txtFechaLanzamiento.getText();
        String tamanoStr = txtTamano.getText();

        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (categoria == null || categoria.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese la categoría.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (plataforma == null || plataforma.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese la plataforma.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (devStr == null) devStr = "";
        if (genStr == null) genStr = "";

        String[] desarrolladores = Arrays.stream(devStr.trim().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        String[] generos = Arrays.stream(genStr.trim().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        if (desarrolladores.length == 0 || generos.length == 0) {
            JOptionPane.showMessageDialog(this, "Ingrese al menos un desarrollador y un género.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tiempoGarantia, stock;
        double precioBase, tamano;
        try {
            tiempoGarantia = Integer.parseInt(tiempoStr.trim());
            precioBase = Double.parseDouble(precioStr.trim());
            stock = Integer.parseInt(stockStr.trim());
            tamano = Double.parseDouble(tamanoStr.trim());
            if(tiempoGarantia < 0 || precioBase < 0 || stock < 0 || tamano < 0) throw new EFormatoInvalido("Verifique que tiempo, precio, stock y tamaño sean números válidos.");
        } catch (EFormatoInvalido e) {
            JOptionPane.showMessageDialog(this, "Verifique que tiempo, precio, stock y tamaño sean números válidos.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date fechaLanzamiento = null;
        if (fechaStr != null && !fechaStr.isBlank()) {
            try {
                fechaLanzamiento = new SimpleDateFormat("dd/MM/yyyy").parse(fechaStr.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Fecha debe estar en formato dd/MM/yyyy.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            controlador.addVideojuego(nombre, descripcion != null ? descripcion : "", categoria, tiempoGarantia, precioBase, stock,
                    desarrolladores, generos, chkMultijugador.isSelected(), fechaLanzamiento, plataforma, tamano);
            JOptionPane.showMessageDialog(this, "Videojuego agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarProductos().setVisible(true);
            dispose();
        } catch (EProductoYaExiste ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto ya existe", JOptionPane.ERROR_MESSAGE);
        } catch (EParametroNulo | ECantidadNegativa | EValorNegativo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
