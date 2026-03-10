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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
        setBounds(100, 100, 620, 640);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(e -> {
            new GestionarProductos().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = UIComponents.crearPanelTarjeta();
        panelForm.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int y = 0;
        agregarFila(panelForm, gbc, y++, "Nombre:", txtNombre = UIComponents.crearCampoTexto(280));
        agregarFila(panelForm, gbc, y++, "Descripción:", txtDescripcion = UIComponents.crearCampoTexto(280));
        agregarFila(panelForm, gbc, y++, "Categoría:", txtCategoria = UIComponents.crearCampoTexto(200));
        agregarFila(panelForm, gbc, y++, "Tiempo garantía (meses):", txtTiempoGarantia = UIComponents.crearCampoTexto(80));
        agregarFila(panelForm, gbc, y++, "Precio base:", txtPrecioBase = UIComponents.crearCampoTexto(120));
        agregarFila(panelForm, gbc, y++, "Stock:", txtStock = UIComponents.crearCampoTexto(80));
        agregarFila(panelForm, gbc, y++, "Desarrollador:", txtDesarrollador = UIComponents.crearCampoTexto(220));
        agregarFila(panelForm, gbc, y++, "Género:", txtGenero = UIComponents.crearCampoTexto(180));
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        JPanel rowMult = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowMult.setOpaque(false);
        rowMult.add(new JLabel("Multijugador:"));
        chkMultijugador = new JCheckBox();
        rowMult.add(chkMultijugador);
        panelForm.add(rowMult, gbc);
        gbc.gridwidth = 1;
        agregarFila(panelForm, gbc, y++, "Fecha lanzamiento (dd/MM/yyyy):", txtFechaLanzamiento = UIComponents.crearCampoTexto(120));
        agregarFila(panelForm, gbc, y++, "Plataforma:", txtPlataforma = UIComponents.crearCampoTexto(150));
        agregarFila(panelForm, gbc, y++, "Tamaño (GB):", txtTamano = UIComponents.crearCampoTexto(80));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        chkDescuentoActivo = new JCheckBox("Activar descuento", true);
        panelForm.add(chkDescuentoActivo, gbc);

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        panelForm.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                panelForm.getBorder(),
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 20)));
        scrollForm.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnAgregar = UIComponents.crearBotonPrincipal("Agregar Videojuego");
        btnAgregar.addActionListener(e -> agregarVideojuego());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void agregarFila(JPanel parent, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_ETIQUETA);
        parent.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        parent.add(field, gbc);
    }

    private void agregarVideojuego() {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String descripcion = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";
        String categoria = txtCategoria.getText() != null ? txtCategoria.getText().trim() : "";
        String tiempoStr = txtTiempoGarantia.getText();
        String precioStr = txtPrecioBase.getText();
        String stockStr = txtStock.getText();
        String desarrollador = txtDesarrollador.getText() != null ? txtDesarrollador.getText().trim() : "";
        String genero = txtGenero.getText() != null ? txtGenero.getText().trim() : "";
        String plataforma = txtPlataforma.getText() != null ? txtPlataforma.getText().trim() : "";
        String fechaStr = txtFechaLanzamiento.getText();
        String tamanoStr = txtTamano.getText();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (categoria.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la categoría.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (plataforma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la plataforma.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (desarrollador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el desarrollador.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (genero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el género.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Validación para Tiempo de Garantía
        if (tiempoStr == null || tiempoStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El campo 'Tiempo de Garantía' es obligatorio.", "Campo faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación para Precio Base
        if (precioStr == null || precioStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El campo 'Precio Base' es obligatorio.", "Campo faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación para Stock
        if (stockStr == null || stockStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El campo 'Stock' es obligatorio.", "Campo faltante", JOptionPane.WARNING_MESSAGE);
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
                    desarrollador, genero, chkMultijugador.isSelected(), fechaLanzamiento, plataforma, tamano, descuentoActivo);
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
