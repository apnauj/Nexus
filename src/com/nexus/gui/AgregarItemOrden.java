package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EStockInsuficiente;
import com.nexus.model.entities.Orden;
import com.nexus.model.entities.Producto;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.util.UUID;

/**
 * Pantalla para añadir un producto a una orden pendiente.
 */
public class AgregarItemOrden extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<String> cmbOrden;
    private JComboBox<String> cmbProducto;
    private JTextField txtCantidad;
    private Orden[] ordenesPendientes;
    private String[] opcionesProductos;

    public AgregarItemOrden() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Añadir Item a Orden");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 480, 340);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarOrdenes().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        ordenesPendientes = controlador.getOrdenesPendientes();
        String[] opcionesOrden = controlador.getOpcionesOrdenesPendientes();
        opcionesProductos = controlador.getOpcionesProductosDisponibles();

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 10));
        panelForm.setPreferredSize(new Dimension(420, 180));

        if (opcionesOrden.length == 0) {
            panelForm.add(new JLabel("No hay órdenes pendientes. Cree una orden primero."));
        } else {
            JPanel rowOrden = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowOrden.add(new JLabel("Orden: "));
            cmbOrden = new JComboBox<>(opcionesOrden);
            cmbOrden.setPreferredSize(new Dimension(280, 25));
            rowOrden.add(cmbOrden);
            panelForm.add(rowOrden);
        }

        if (opcionesProductos.length == 0) {
            panelForm.add(new JLabel("No hay productos con stock disponible."));
        } else {
            JPanel rowProducto = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowProducto.add(new JLabel("Producto: "));
            cmbProducto = new JComboBox<>(opcionesProductos);
            cmbProducto.setPreferredSize(new Dimension(280, 25));
            rowProducto.add(cmbProducto);
            panelForm.add(rowProducto);
        }

        JPanel rowCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowCantidad.add(new JLabel("Cantidad: "));
        txtCantidad = new JTextField(8);
        txtCantidad.setForeground(Color.BLACK);
        txtCantidad.setBackground(Color.WHITE);
        txtCantidad.setCaretColor(Color.BLACK);
        rowCantidad.add(txtCantidad);
        panelForm.add(rowCantidad);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAñadir = new JButton("Añadir a la orden");
        btnAñadir.addActionListener(e -> añadirItem());
        panelBotones.add(btnAñadir);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void añadirItem() {
        if (ordenesPendientes == null || ordenesPendientes.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay órdenes pendientes disponibles.", "Sin órdenes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (opcionesProductos == null || opcionesProductos.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos con stock disponible.", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbProducto == null) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles para añadir.", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cantidadStr = txtCantidad.getText();
        if (cantidadStr == null || cantidadStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtCantidad.requestFocus();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr.trim());
            if (cantidad <= 0) throw new EFormatoInvalido("Debe ser positivo");
        } catch (EFormatoInvalido e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero positivo.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtCantidad.requestFocus();
            return;
        }

        int idxOrden = cmbOrden.getSelectedIndex();
        String nombreProducto = (String) cmbProducto.getSelectedItem();
        UUID idOrden = ordenesPendientes[idxOrden].getIdPedido();

        try {
            controlador.addItemToOrden(idOrden, nombreProducto, cantidad);
            Producto p = controlador.searchProducto(nombreProducto);
            JOptionPane.showMessageDialog(this, "Producto añadido correctamente.\n\n" + cantidad + " x " + nombreProducto + "\nStock restante: " + p.getStock(), "Item añadido", JOptionPane.INFORMATION_MESSAGE);
            txtCantidad.setText("");
        } catch (EOrdenNoEncontrada ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Orden no encontrada", JOptionPane.ERROR_MESSAGE);
        } catch (EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (EStockInsuficiente ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Stock insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (ECantidadNegativa | EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
