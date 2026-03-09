package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoYaExiste;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.Producto;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nexus.NexusApplication.addGuardarAlCerrar;

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
    private JTextField txtDesarrollador;
    private JTextField txtGenero;
    private JCheckBox chkMultijugador;
    private JTextField txtFechaLanzamiento;
    private JTextField txtPlataforma;
    private JTextField txtTamano;
    private JCheckBox chkDescuentoActivo;

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
        addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, UITheme.VENTANA_FORMULARIO_ANCHO, UITheme.VENTANA_FORMULARIO_ALTO);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarProductos().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, UITheme.ESPACIADO));
        panelForm.setBackground(UITheme.FONDO_PANEL);

        panelForm.add(crearFila("Nombre: ", txtNombre = crearTextField(28)));
        panelForm.add(crearFila("Descripción: ", txtDescripcion = crearTextField(28)));
        panelForm.add(crearFila("Categoría: ", txtCategoria = crearTextField(22)));
        panelForm.add(crearFila("Tiempo garantía (meses): ", txtTiempoGarantia = crearTextField(6)));
        panelForm.add(crearFila("Precio base: ", txtPrecioBase = crearTextField(12)));
        panelForm.add(crearFila("Stock: ", txtStock = crearTextField(6)));
        panelForm.add(crearFila("Desarrollador: ", txtDesarrollador = crearTextField(28)));
        panelForm.add(crearFila("Género: ", txtGenero = crearTextField(22)));
        JPanel rowMult = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowMult.add(new JLabel("Multijugador: "));
        chkMultijugador = new JCheckBox();
        rowMult.add(chkMultijugador);
        panelForm.add(rowMult);
        panelForm.add(crearFila("Fecha lanzamiento (dd/MM/yyyy): ", txtFechaLanzamiento = crearTextField(12)));
        panelForm.add(crearFila("Plataforma: ", txtPlataforma = crearTextField(15)));
        panelForm.add(crearFila("Tamaño (GB): ", txtTamano = crearTextField(8)));
        JPanel rowDescuento = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkDescuentoActivo = new JCheckBox("Activar descuento", true);
        rowDescuento.add(chkDescuentoActivo);
        panelForm.add(rowDescuento);

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
        String desarrollador = txtDesarrollador.getText();
        String genero = txtGenero.getText();
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
        if (desarrollador == null || desarrollador.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el desarrollador.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (genero == null || genero.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el género.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tiempoStr == null || tiempoStr.isBlank() || precioStr == null || precioStr.isBlank()
                || stockStr == null || stockStr.isBlank() || tamanoStr == null || tamanoStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos numéricos (tiempo garantía, precio base, stock, tamaño).", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tiempoGarantia, stock;
        double precioBase, tamano;
        try {
            tiempoGarantia = Integer.parseInt(tiempoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El tiempo de garantía debe ser un número entero válido (ej: 12).", "Tiempo de garantía inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            precioBase = Double.parseDouble(precioStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio base debe ser un número válido (ej: 100000 o 99.99).", "Precio base inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            stock = Integer.parseInt(stockStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero válido (ej: 10).", "Stock inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            tamano = Double.parseDouble(tamanoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El tamaño debe ser un número válido en GB (ej: 50 o 12.5).", "Tamaño inválido", JOptionPane.ERROR_MESSAGE);
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
            boolean descuentoActivo = chkDescuentoActivo.isSelected();
            Producto producto = controlador.addVideojuego(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock,
                    desarrollador.trim(), genero.trim(), chkMultijugador.isSelected(), fechaLanzamiento, plataforma, tamano, descuentoActivo);
            String msg = descuentoActivo
                    ? String.format("Videojuego agregado correctamente.%nSe aplicó un descuento del %d%% (según stock y antigüedad).", (int) Math.round(producto.getDescuento() * 100))
                    : "Videojuego agregado correctamente. Sin descuento aplicado.";
            JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
