package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.enums.Rol;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pantalla para actualizar un producto existente (descripción, categoría, stock, etc.).
 */
public class ActualizarProducto extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCategoria;
    private JTextField txtTiempoGarantia;
    private JTextField txtPrecioBase;
    private JTextField txtStock;

    public ActualizarProducto() {
        this(null);
    }

    public ActualizarProducto(String nombreProducto) {
        controlador = StoreController.getInstance();
        setTitle("Nexus Store - Actualizar Producto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 540, 420);

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

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 8));

        panelForm.add(crearFilaDosLineas("Nombre del producto (para buscar):", txtNombre = crearTextField(40)));
        if (nombreProducto != null && !nombreProducto.isBlank()) {
            txtNombre.setText(nombreProducto);
            txtNombre.setEditable(false);
        }
        panelForm.add(crearFila("Nueva descripción: ", txtDescripcion = crearTextField(30)));
        panelForm.add(crearFila("Nueva categoría: ", txtCategoria = crearTextField(28)));
        panelForm.add(crearFila("Tiempo garantía (meses): ", txtTiempoGarantia = crearTextField(8)));
        panelForm.add(crearFila("Precio base: ", txtPrecioBase = crearTextField(12)));
        JPanel rowStock = crearFila("Stock: ", txtStock = crearTextField(8));
        if (controlador.getCurrentUser() != null && controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            txtStock.setEnabled(false);
            txtStock.setToolTipText("No tiene permisos para modificar el stock.");
        }
        panelForm.add(rowStock);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        panelBotones.add(btnActualizar);
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

    private void actualizar() {
        String nombre = txtNombre.getText();
        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String desc = txtDescripcion.getText();
        String cat = txtCategoria.getText();
        int tiempoGarantia = -1;
        double precioBase = -1;
        int stock = -1;

        if (txtTiempoGarantia.getText() != null && !txtTiempoGarantia.getText().isBlank()) {
            try {
                tiempoGarantia = Integer.parseInt(txtTiempoGarantia.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tiempo de garantía debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (txtPrecioBase.getText() != null && !txtPrecioBase.getText().isBlank()) {
            try {
                precioBase = Double.parseDouble(txtPrecioBase.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Precio debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (txtStock.getText() != null && !txtStock.getText().isBlank()) {
            try {
                stock = Integer.parseInt(txtStock.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Stock debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (controlador.getCurrentUser() != null && controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            stock = -1;
        }

        try {
            controlador.actualizarProducto(nombre, desc, cat, tiempoGarantia, precioBase, stock);
            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (EParametroNulo | EValorNegativo | ECantidadNegativa ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
