package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EEstadoOrdenInvalido;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.model.entities.Orden;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.UUID;

/**
 * Pantalla para eliminar un item de una orden pendiente.
 * Usa los métodos del controlador para obtener órdenes y productos (sin List/ArrayList).
 */
public class EliminarItemOrden extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<String> cmbOrden;
    private JComboBox<String> cmbProducto;
    private Orden[] ordenesPendientes;
    private String[] opcionesProductosEnOrden;

    public EliminarItemOrden() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Eliminar Item de Orden");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 480, 280);
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
        panelForm.setPreferredSize(new Dimension(420, 120));

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
            rowProducto.add(new JLabel("Producto a eliminar: "));
            opcionesProductosEnOrden = obtenerProductosEnOrdenSeleccionada();
            cmbProducto = new JComboBox<>(opcionesProductosEnOrden);
            cmbProducto.setPreferredSize(new Dimension(280, 25));
            rowProducto.add(cmbProducto);
            panelForm.add(rowProducto);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEliminar = new JButton("Eliminar de la orden");
        btnEliminar.addActionListener(e -> eliminarItem());
        panelBotones.add(btnEliminar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    //TODO: pasar al controller
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
    //TODO: deberia estar en el controller
    private void actualizarProductosEnOrden() {
        if (cmbOrden == null || cmbProducto == null) return;
        opcionesProductosEnOrden = obtenerProductosEnOrdenSeleccionada();
        cmbProducto.removeAllItems();
        for (String n : opcionesProductosEnOrden) {
            cmbProducto.addItem(n);
        }
    }

    private void eliminarItem() {
        if (ordenesPendientes == null || ordenesPendientes.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay órdenes pendientes disponibles.", "Sin órdenes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (opcionesProductosEnOrden == null || opcionesProductosEnOrden.length == 0) {
            JOptionPane.showMessageDialog(this, "La orden seleccionada no tiene productos.", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbProducto == null) {
            JOptionPane.showMessageDialog(this, "No hay productos para eliminar.", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreProducto = (String) cmbProducto.getSelectedItem();
        if (nombreProducto == null || nombreProducto.isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idxOrden = cmbOrden.getSelectedIndex();
        UUID idOrden = ordenesPendientes[idxOrden].getIdPedido();

        try {
            controlador.removeItemOrden(idOrden, nombreProducto);
            JOptionPane.showMessageDialog(this, "Item eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarOrdenes().setVisible(true);
            dispose();
        } catch (EOrdenNoEncontrada | EProductoNoEncontrado | EParametroNulo | EEstadoOrdenInvalido ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
