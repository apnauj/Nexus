package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.Cliente;
import com.nexus.model.entities.Orden;
import com.nexus.model.enums.Estado;
import com.nexus.model.enums.Rol;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pantalla para gestionar órdenes.
 * Layout: JFrame con JTable de órdenes y botones según rol.
 * - ADMIN y EMPLEADO_VENTAS: Añadir Orden, Añadir Item, Eliminar Item, Verificar pago
 * - GESTOR_INVENTARIO: solo visualización (tabla)
 */
public class GestionarOrdenes extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private StoreController controlador;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;

    public GestionarOrdenes() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Gestionar Órdenes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 700, 450);
        setResizable(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        // Panel superior: Regresar
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> volverAlMenu());
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        // Tabla de órdenes
        String[] columnas = { "ID", "Cliente", "Fecha", "Estado", "Total", "Valor Pagado", "Cambio" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollTabla = new JScrollPane(tablaOrdenes);
        contentPane.add(scrollTabla, BorderLayout.CENTER);

        // Panel de botones según rol
        Rol rol = controlador.getCurrentUser().getRol();
        boolean puedeModificar = (rol == Rol.ADMIN || rol == Rol.EMPLEADO_VENTAS);

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 5, 5));
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        if (puedeModificar) {
            JButton btnAñadirOrden = new JButton("Añadir Orden");
            btnAñadirOrden.addActionListener(e -> abrirAgregarOrden());
            panelBotones.add(btnAñadirOrden);

            JButton btnAñadirItem = new JButton("Añadir Item a la Orden");
            btnAñadirItem.addActionListener(e -> abrirAgregarItemOrden());
            panelBotones.add(btnAñadirItem);

            JButton btnEliminarItem = new JButton("Eliminar Item de la Orden");
            btnEliminarItem.addActionListener(e -> abrirEliminarItemOrden());
            panelBotones.add(btnEliminarItem);

            JButton btnModificarCantidad = new JButton("Modificar Cantidad de Item");
            btnModificarCantidad.addActionListener(e -> abrirModificarCantidadItem());
            panelBotones.add(btnModificarCantidad);

            JButton btnVerificarPago = new JButton("Registrar pago");
            btnVerificarPago.addActionListener(e -> verificarPagoOrden());
            panelBotones.add(btnVerificarPago);
        }

        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        Orden[] ordenes = controlador.getHistorial();
        for (Orden o : ordenes) {
            Cliente c = o.getCliente();
            String clienteStr = (c != null) ? c.getNombre() + " " + c.getApellido() : "-";
            String idStr = o.getIdPedido().toString().substring(0, Math.min(8, o.getIdPedido().toString().length())) + "...";
            double total = o.getTotal();
            if (o.getItems() != null && o.getItems().length > 0 && total == 0) {
                total = o.calcularTotal();
            }
            String valorPagadoStr = (o.getValorPagado() > 0) ? String.format("%.2f", o.getValorPagado()) : "-";
            String cambioStr = (o.getEstado() != Estado.PENDIENTE) ? String.format("%.2f", o.getCambio()) : "-";
            modeloTabla.addRow(new Object[]{
                    idStr,
                    clienteStr,
                    o.getFecha(),
                    o.getEstado().toString(),
                    String.format("%.2f", total),
                    valorPagadoStr,
                    cambioStr
            });
        }
    }

    private void volverAlMenu() {
        new MenuPrincipalFrame().setVisible(true);
        dispose();
    }

    private void abrirAgregarOrden() {
        try {
            AgregarOrden ao = new AgregarOrden();
            ao.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirAgregarItemOrden() {
        try {
            AgregarItemOrden aio = new AgregarItemOrden();
            aio.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirEliminarItemOrden() {
        try {
            EliminarItemOrden eio = new EliminarItemOrden();
            eio.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirModificarCantidadItem() {
        try {
            ModificarCantidadItem mci = new ModificarCantidadItem();
            mci.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarPagoOrden() {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Orden[] ordenes = controlador.getHistorial();
        if (fila >= ordenes.length) {
            JOptionPane.showMessageDialog(this, "Orden no válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Orden orden = ordenes[fila];
        if (orden.getEstado() != Estado.PENDIENTE) {
            JOptionPane.showMessageDialog(this, "Solo se puede verificar pago de órdenes en estado PENDIENTE.",
                    "Estado inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (orden.getItems() == null || orden.getItems().length == 0) {
            JOptionPane.showMessageDialog(this, "La orden no tiene items. Añada productos antes de verificar.",
                    "Orden vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalOrden = orden.getTotal();
        if (totalOrden == 0 && orden.getItems() != null && orden.getItems().length > 0) {
            totalOrden = orden.calcularTotal();
        }

        String input = JOptionPane.showInputDialog(this,
                "Total a pagar: $" + String.format("%.2f", totalOrden) + "\n\nValor pagado por el cliente:",
                "Verificar pago", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isBlank()) {
            return;
        }

        try {
            double valorPagado = Double.parseDouble(input.trim());
            if (valorPagado < 0) {
                throw new EFormatoInvalido("El valor no puede ser negativo");
            }
            controlador.verificarPago(orden.getIdPedido(), valorPagado);

            // Obtener la orden actualizada para mostrar valor pagado y cambio
            Orden ordenActualizada = controlador.searchOrden(orden.getIdPedido());
            double cambio = ordenActualizada.getCambio();
            String msg;
            if (ordenActualizada.getEstado() == Estado.APROBADO) {
                msg = "Pago aprobado.\n\nValor pagado: $" + String.format("%.2f", valorPagado)
                        + "\nTotal: $" + String.format("%.2f", totalOrden)
                        + "\n\nCambio a devolver al cliente: $" + String.format("%.2f", cambio);
            } else {
                msg = "Pago rechazado (valor insuficiente).\n\nValor pagado: $" + String.format("%.2f", valorPagado)
                        + "\nTotal requerido: $" + String.format("%.2f", totalOrden)
                        + "\n\nSe devuelve al cliente: $" + String.format("%.2f", cambio);
            }
            JOptionPane.showMessageDialog(this, msg, "Resultado del pago", JOptionPane.INFORMATION_MESSAGE);
            actualizarTabla();
        } catch (EFormatoInvalido e) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido.",
                    "Formato inválido", JOptionPane.ERROR_MESSAGE);
        } catch (EOrdenNoEncontrada | EParametroNulo | EValorNegativo | EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
