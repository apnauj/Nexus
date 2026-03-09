package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoYaExiste;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.Producto;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

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
        setBounds(100, 100, 480, 420);
        setResizable(false);
        setLocationRelativeTo(null);

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
        panelForm.setPreferredSize(new Dimension(420, 280));

        panelForm.add(crearFila("Nombre: ", txtNombre = crearTextField(25)));
        panelForm.add(crearFila("Descripción: ", txtDescripcion = crearTextField(25)));
        panelForm.add(crearFila("Categoría: ", txtCategoria = crearTextField(20)));
        panelForm.add(crearFila("Tiempo garantía (meses): ", txtTiempoGarantia = crearTextField(5)));
        panelForm.add(crearFila("Precio base: ", txtPrecioBase = crearTextField(10)));
        panelForm.add(crearFila("Stock: ", txtStock = crearTextField(5)));
        panelForm.add(crearFila("Consumo (W): ", txtConsumo = crearTextField(8)));
        panelForm.add(crearFila("Fabricante: ", txtFabricante = crearTextField(20)));

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar Hardware");
        btnAgregar.addActionListener(e -> agregarHardware());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearFila(String label, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    private void agregarHardware() {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String categoria = txtCategoria.getText();
        String tiempoStr = txtTiempoGarantia.getText();
        String precioStr = txtPrecioBase.getText();
        String stockStr = txtStock.getText();
        String consumoStr = txtConsumo.getText();
        String fabricante = txtFabricante.getText();

        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (categoria == null || categoria.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese la categoría.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fabricante == null || fabricante.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el fabricante.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
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
            Producto producto = controlador.addHardware(nombre, descripcion != null ? descripcion : "", categoria, tiempoGarantia, precioBase, stock, consumo, fabricante);
            int descuentoPct = (int) Math.round(producto.getDescuento() * 100);
            String msg = String.format("Hardware agregado correctamente.%nSe aplicó un descuento del %d%% (según stock y consumo).", descuentoPct);
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
