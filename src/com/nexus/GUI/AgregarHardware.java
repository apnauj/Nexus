package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoYaExiste;
import com.nexus.exceptions.EValorNegativo;

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
            precioBase = Double.parseDouble(precioStr.trim());
            stock = Integer.parseInt(stockStr.trim());
            consumo = Float.parseFloat(consumoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique que tiempo, precio, stock y consumo sean números válidos.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controlador.addHardware(nombre, descripcion != null ? descripcion : "", categoria, tiempoGarantia, precioBase, stock, consumo, fabricante);
            JOptionPane.showMessageDialog(this, "Hardware agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
