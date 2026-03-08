package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.model.entities.Cliente;
import com.nexus.model.entities.Orden;
import com.nexus.model.entities.OrdenItem;
import com.nexus.model.entities.Producto;
import com.nexus.model.enums.Estado;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pantalla para eliminar un item de una orden pendiente.
 */
public class EliminarItemOrden extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<String> cmbOrden;
    private JComboBox<String> cmbProducto;
    private List<Orden> ordenesPendientes;
    private List<String> productosEnOrden;

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

        ordenesPendientes = new ArrayList<>();
        for (Orden o : controlador.getHistorial()) {
            if (o.getEstado() == Estado.PENDIENTE && o.getItems() != null && o.getItems().length > 0) {
                ordenesPendientes.add(o);
            }
        }

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 10));
        panelForm.setPreferredSize(new Dimension(420, 120));

        if (ordenesPendientes.isEmpty()) {
            panelForm.add(new JLabel("No hay órdenes pendientes con items."));
        } else {
            JPanel rowOrden = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowOrden.add(new JLabel("Orden: "));
            String[] opciones = new String[ordenesPendientes.size()];
            for (int i = 0; i < ordenesPendientes.size(); i++) {
                Orden o = ordenesPendientes.get(i);
                Cliente c = o.getCliente();
                String clienteStr = (c != null) ? c.getNombre() + " " + c.getApellido() : "?";
                opciones[i] = clienteStr + " - " + o.getFecha();
            }
            cmbOrden = new JComboBox<>(opciones);
            cmbOrden.setPreferredSize(new Dimension(280, 25));
            cmbOrden.addActionListener(e -> actualizarProductosEnOrden());
            rowOrden.add(cmbOrden);
            panelForm.add(rowOrden);

            JPanel rowProducto = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowProducto.add(new JLabel("Producto a eliminar: "));
            productosEnOrden = new ArrayList<>();
            actualizarListaProductos();
            cmbProducto = new JComboBox<>(productosEnOrden.toArray(new String[0]));
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

    private void actualizarListaProductos() {
        productosEnOrden.clear();
        if (ordenesPendientes != null && !ordenesPendientes.isEmpty()) {
            Orden o = ordenesPendientes.get(0);
            if (o.getItems() != null) {
                for (OrdenItem item : o.getItems()) {
                    if (item.getProducto() != null) {
                        String n = item.getProducto().getNombre();
                        if (n != null && !n.isBlank()) productosEnOrden.add(n);
                    }
                }
            }
        }
    }

    private void actualizarProductosEnOrden() {
        if (cmbOrden == null || cmbProducto == null) return;
        int idx = cmbOrden.getSelectedIndex();
        if (idx < 0 || idx >= ordenesPendientes.size()) return;

        productosEnOrden.clear();
        Orden o = ordenesPendientes.get(idx);
        if (o.getItems() != null) {
            for (OrdenItem item : o.getItems()) {
                if (item.getProducto() != null) {
                    String n = item.getProducto().getNombre();
                    if (n != null && !n.isBlank()) productosEnOrden.add(n);
                }
            }
        }
        cmbProducto.removeAllItems();
        for (String n : productosEnOrden) cmbProducto.addItem(n);
    }

    private void eliminarItem() {
        if (ordenesPendientes == null || ordenesPendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay órdenes pendientes con items.", "Sin órdenes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idxOrden = cmbOrden.getSelectedIndex();
        String nombreProducto = (String) cmbProducto.getSelectedItem();
        if (nombreProducto == null || nombreProducto.isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UUID idOrden = ordenesPendientes.get(idxOrden).getIdPedido();

        try {
            controlador.removeItemOrden(idOrden, nombreProducto);
            JOptionPane.showMessageDialog(this, "Item eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarOrdenes().setVisible(true);
            dispose();
        } catch (EOrdenNoEncontrada | EProductoNoEncontrado | EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
