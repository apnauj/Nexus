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
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Pantalla para agregar un producto tipo Hardware.
 */
public class AgregarHardware extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCategoria;
    private JTextField txtTiempoGarantia;
    private JTextField txtPrecioBase;
    private JTextField txtStock;
    private JTextField txtConsumo;
    private JTextField txtFabricante;
    private JCheckBox chkDescuentoActivo;

    public AgregarHardware() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Agregar Hardware");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 560, 580);
        setResizable(true);
        setLocationRelativeTo(null);

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
        agregarFila(panelForm, gbc, y++, "Consumo (W):", txtConsumo = UIComponents.crearCampoTexto(100));
        agregarFila(panelForm, gbc, y++, "Fabricante:", txtFabricante = UIComponents.crearCampoTexto(200));
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        chkDescuentoActivo = new JCheckBox("Activar descuento", true);
        panelForm.add(chkDescuentoActivo, gbc);

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        scrollForm.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollForm.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnAgregar = UIComponents.crearBotonPrincipal("Agregar Hardware");
        btnAgregar.addActionListener(e -> agregarHardware());
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

    private void agregarHardware() {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String descripcion = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";
        String categoria = txtCategoria.getText() != null ? txtCategoria.getText().trim() : "";
        String tiempoStr = txtTiempoGarantia.getText();
        String precioStr = txtPrecioBase.getText();
        String stockStr = txtStock.getText();
        String consumoStr = txtConsumo.getText();
        String fabricante = txtFabricante.getText() != null ? txtFabricante.getText().trim() : "";

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (categoria.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la categoría.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fabricante.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el fabricante.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
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
        double precioBase;
        float consumo;
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
            consumo = Float.parseFloat(consumoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El consumo debe ser un número válido en vatios (ej: 150 o 85.5).", "Consumo inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean descuentoActivo = chkDescuentoActivo.isSelected();
            Producto producto = controlador.addHardware(nombre, descripcion, categoria, tiempoGarantia, precioBase, stock, consumo, fabricante, descuentoActivo);
            String msg = descuentoActivo
                    ? String.format("Hardware agregado correctamente.%nSe aplicó un descuento del %d%% (según stock y consumo).", (int) Math.round(producto.getDescuento() * 100))
                    : "Hardware agregado correctamente. Sin descuento aplicado.";
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
