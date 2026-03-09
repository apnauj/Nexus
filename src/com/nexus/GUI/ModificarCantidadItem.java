package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EEstadoOrdenInvalido;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EStockInsuficiente;
import com.nexus.model.entities.Orden;

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
 * Pantalla para modificar la cantidad de un producto en una orden pendiente.
 */
public class ModificarCantidadItem extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<String> cmbOrden;
    private JComboBox<String> cmbProducto;
    private JTextField txtCantidad;
    private Orden[] ordenesPendientes;
    private String[] opcionesProductosEnOrden;

    public ModificarCantidadItem() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Modificar Cantidad de Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 480, 320);
        setLocationRelativeTo(null);
        setResizable(false);

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

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 10));
        panelForm.setPreferredSize(new Dimension(420, 160));

        if (opcionesOrden.length == 0) {
            panelForm.add(new JLabel("No hay órdenes pendientes. Cree una orden y añada items primero."));
        } else {
            JPanel rowOrden = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowOrden.add(new JLabel("Orden: "));
            cmbOrden = new JComboBox<>(opcionesOrden);
            cmbOrden.setPreferredSize(new Dimension(280, 25));
            cmbOrden.addActionListener(e -> actualizarProductosEnOrden());
            rowOrden.add(cmbOrden);
            panelForm.add(rowOrden);

            JPanel rowProducto = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowProducto.add(new JLabel("Producto: "));
            opcionesProductosEnOrden = obtenerProductosEnOrdenSeleccionada();
            cmbProducto = new JComboBox<>(opcionesProductosEnOrden);
            cmbProducto.setPreferredSize(new Dimension(280, 25));
            rowProducto.add(cmbProducto);
            panelForm.add(rowProducto);

            JPanel rowCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowCantidad.add(new JLabel("Nueva cantidad: "));
            txtCantidad = new JTextField(8);
            txtCantidad.setForeground(Color.BLACK);
            txtCantidad.setBackground(Color.WHITE);
            txtCantidad.setCaretColor(Color.BLACK);
            rowCantidad.add(txtCantidad);
            panelForm.add(rowCantidad);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnModificar = new JButton("Modificar cantidad");
        btnModificar.addActionListener(e -> modificarCantidad());
        panelBotones.add(btnModificar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private String[] obtenerProductosEnOrdenSeleccionada() {
        if (ordenesPendientes == null || ordenesPendientes.length == 0) return new String[0];
        int idx = cmbOrden != null ? cmbOrden.getSelectedIndex() : 0;
        if (idx < 0 || idx >= ordenesPendientes.length) return new String[0];
        try {
            UUID idOrden = ordenesPendientes[idx].getIdPedido();
            return controlador.getOpcionesProductosEnOrden(idOrden);
        } catch (EParametroNulo | EOrdenNoEncontrada e) {
            return new String[0];
        }
    }

    private void actualizarProductosEnOrden() {
        if (cmbOrden == null || cmbProducto == null) return;
        opcionesProductosEnOrden = obtenerProductosEnOrdenSeleccionada();
        cmbProducto.removeAllItems();
        for (String n : opcionesProductosEnOrden) {
            cmbProducto.addItem(n);
        }
    }

    private void modificarCantidad() {
        if (ordenesPendientes == null || ordenesPendientes.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay órdenes pendientes disponibles.", "Sin órdenes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (opcionesProductosEnOrden == null || opcionesProductosEnOrden.length == 0) {
            JOptionPane.showMessageDialog(this, "La orden seleccionada no tiene productos.", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbProducto == null) {
            JOptionPane.showMessageDialog(this, "No hay productos para modificar.", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreProducto = (String) cmbProducto.getSelectedItem();
        if (nombreProducto == null || nombreProducto.isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cantidadStr = txtCantidad.getText();
        if (cantidadStr == null || cantidadStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese la nueva cantidad.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtCantidad.requestFocus();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr.trim());
            if (cantidad <= 0) throw new NumberFormatException("Debe ser positivo");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero positivo.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtCantidad.requestFocus();
            return;
        }

        int idxOrden = cmbOrden.getSelectedIndex();
        UUID idOrden = ordenesPendientes[idxOrden].getIdPedido();

        try {
            controlador.modificarCantidadItem(idOrden, nombreProducto, cantidad);
            JOptionPane.showMessageDialog(this, "Cantidad modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarOrdenes().setVisible(true);
            dispose();
        } catch (EOrdenNoEncontrada | EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (EStockInsuficiente ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Stock insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (ECantidadNegativa | EEstadoOrdenInvalido | EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
