package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EOrdenNoEncontrada;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.Cliente;
import com.nexus.model.entities.Orden;
import com.nexus.model.entities.OrdenItem;
import com.nexus.model.enums.Estado;
import com.nexus.model.enums.Rol;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Dialog;
import java.awt.Insets;
import java.awt.Window;

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
        setBounds(100, 100, UITheme.VENTANA_TABLA_ANCHO, UITheme.VENTANA_TABLA_ALTO);
        setResizable(true);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        // Panel superior: Regresar
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
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
        tablaOrdenes.getTableHeader().setFont(UITheme.FONT_ENCABEZADO_TABLA);
        tablaOrdenes.setRowHeight(24);
        JScrollPane scrollTabla = new JScrollPane(tablaOrdenes);
        scrollTabla.setMinimumSize(new Dimension(400, 150)); // ~5 filas + header
        contentPane.add(scrollTabla, BorderLayout.CENTER);

        // Panel de botones según rol
        Rol rol = controlador.getCurrentUser().getRol();
        boolean puedeModificar = (rol == Rol.ADMIN || rol == Rol.EMPLEADO_VENTAS);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.insets = new Insets(6, 0, 6, 0);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1.0;

        JButton btnVerDetalle = UIComponents.crearBotonMenu("Ver detalle de la orden");
        btnVerDetalle.addActionListener(e -> mostrarDetalleOrden());
        panelBotones.add(btnVerDetalle, gbcBtn);
        gbcBtn.gridy++;

        if (puedeModificar) {
            JButton btnAñadirOrden = UIComponents.crearBotonMenu("Añadir Orden");
            btnAñadirOrden.addActionListener(e -> abrirAgregarOrden());
            panelBotones.add(btnAñadirOrden, gbcBtn);
            gbcBtn.gridy++;

            JButton btnAñadirItem = UIComponents.crearBotonMenu("Añadir Item a la Orden");
            btnAñadirItem.addActionListener(e -> abrirAgregarItemOrden());
            panelBotones.add(btnAñadirItem, gbcBtn);
            gbcBtn.gridy++;

            JButton btnEliminarItem = UIComponents.crearBotonMenu("Eliminar Item de la Orden");
            btnEliminarItem.addActionListener(e -> abrirEliminarItemOrden());
            panelBotones.add(btnEliminarItem, gbcBtn);
            gbcBtn.gridy++;

            JButton btnModificarCantidad = UIComponents.crearBotonMenu("Modificar Cantidad de Item");
            btnModificarCantidad.addActionListener(e -> abrirModificarCantidadItem());
            panelBotones.add(btnModificarCantidad, gbcBtn);
            gbcBtn.gridy++;

            JButton btnVerificarPago = UIComponents.crearBotonPrincipal("Registrar pago");
            btnVerificarPago.addActionListener(e -> verificarPagoOrden());
            panelBotones.add(btnVerificarPago, gbcBtn);
        }

        contentPane.add(panelBotones, BorderLayout.SOUTH);
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

    private void mostrarDetalleOrden() {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Orden[] ordenes = controlador.getHistorial();
        if (fila >= ordenes.length) return;
        Orden o = ordenes[fila];
        if (o == null) return;

        DetalleOrdenDialog dlg = new DetalleOrdenDialog(this, o);
        dlg.setVisible(true);
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

        VerificarPagoDialog dlg = new VerificarPagoDialog(this, totalOrden);
        dlg.setVisible(true);
        Double valorPagado = dlg.getValorPagado();
        if (valorPagado == null) {
            return;
        }

        if (valorPagado < 0) {
            JOptionPane.showMessageDialog(this, "El valor no puede ser negativo.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
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
        } catch (EOrdenNoEncontrada | EParametroNulo | EValorNegativo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Diálogo modal para verificar pago de una orden.
     */
    private static class VerificarPagoDialog extends JDialog {
        private Double valorPagadoResultado;

        VerificarPagoDialog(Window parent, double totalOrden) {
            super(parent, "Verificar pago", Dialog.ModalityType.APPLICATION_MODAL);
            setSize(420, 220);
            setLocationRelativeTo(parent);
            setResizable(false);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel content = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
            content.setBackground(UITheme.FONDO_PANEL);
            content.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
            setContentPane(content);

            JPanel panelForm = UIComponents.crearPanelTarjeta();
            panelForm.setLayout(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.insets = new java.awt.Insets(10, 10, 10, 10);
            gbc.anchor = java.awt.GridBagConstraints.WEST;

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            JLabel lblTotal = new JLabel("Total a pagar: $" + String.format("%.2f", totalOrden));
            lblTotal.setFont(UITheme.FONT_SUBTITULO);
            lblTotal.setForeground(UITheme.COLOR_PRINCIPAL);
            panelForm.add(lblTotal, gbc);
            gbc.gridwidth = 1;

            gbc.gridy = 1;
            JLabel lblValor = new JLabel("Valor pagado por el cliente:");
            lblValor.setFont(UITheme.FONT_ETIQUETA);
            panelForm.add(lblValor, gbc);
            JTextField txtValor = UIComponents.crearCampoTexto(180);
            gbc.gridx = 1;
            panelForm.add(txtValor, gbc);

            content.add(panelForm, BorderLayout.CENTER);

            JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelBtn.setOpaque(false);
            JButton btnCancelar = UIComponents.crearBotonLink("Cancelar");
            btnCancelar.addActionListener(e -> {
                valorPagadoResultado = null;
                dispose();
            });
            JButton btnVerificar = UIComponents.crearBotonPrincipal("Verificar");
            btnVerificar.addActionListener(e -> {
                String txt = txtValor.getText();
                if (txt == null || txt.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Ingrese el valor pagado.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    double val = Double.parseDouble(txt.trim());
                    valorPagadoResultado = val;
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido (ej: 150000 o 99.50).", "Formato inválido", JOptionPane.ERROR_MESSAGE);
                }
            });
            panelBtn.add(btnCancelar);
            panelBtn.add(btnVerificar);
            content.add(panelBtn, BorderLayout.SOUTH);
        }

        Double getValorPagado() {
            return valorPagadoResultado;
        }
    }

    /**
     * Diálogo modal para mostrar el detalle de una orden.
     */
    private static class DetalleOrdenDialog extends JDialog {
        DetalleOrdenDialog(Window parent, Orden orden) {
            super(parent, "Detalle de la orden", Dialog.ModalityType.APPLICATION_MODAL);
            setSize(520, 420);
            setLocationRelativeTo(parent);
            setResizable(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel content = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
            content.setBackground(UITheme.FONDO_PANEL);
            content.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
            setContentPane(content);

            JPanel panelInfo = new JPanel(new java.awt.GridLayout(5, 2, 8, 6));
            panelInfo.setBackground(java.awt.Color.WHITE);
            panelInfo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDE),
                    new EmptyBorder(12, 12, 12, 12)));

            Cliente c = orden.getCliente();
            String clienteStr = (c != null) ? c.getNombre() + " " + c.getApellido() : "-";

            agregarFilaInfo(panelInfo, "ID:", orden.getIdPedido().toString());
            agregarFilaInfo(panelInfo, "Cliente:", clienteStr);
            agregarFilaInfo(panelInfo, "Fecha:", orden.getFecha());
            agregarFilaInfo(panelInfo, "Estado:", orden.getEstado().toString());
            agregarFilaInfo(panelInfo, "Método de pago:", orden.getMetodoPago() != null ? orden.getMetodoPago().toString() : "-");

            content.add(panelInfo, BorderLayout.NORTH);

            String[] cols = { "Producto", "Cant.", "P. Unit.", "Subtotal" };
            javax.swing.table.DefaultTableModel modelItems = new javax.swing.table.DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            OrdenItem[] items = orden.getItems();
            double totalOrden = 0;
            if (items != null && items.length > 0) {
                for (OrdenItem item : items) {
                    String nombreProd = item.getProducto() != null ? item.getProducto().getNombre() : "?";
                    int cant = item.getCantidad();
                    double subtotal = item.calcularSubtotal();
                    totalOrden += subtotal;
                    double pUnit = cant > 0 ? subtotal / cant : 0;
                    modelItems.addRow(new Object[]{
                            nombreProd, cant,
                            String.format("$%.2f", pUnit),
                            String.format("$%.2f", subtotal)
                    });
                }
            }

            JTable tablaItems = new JTable(modelItems);
            tablaItems.setRowHeight(24);
            tablaItems.setFont(UITheme.FONT_NORMAL);
            tablaItems.getTableHeader().setFont(UITheme.FONT_ENCABEZADO_TABLA);
            tablaItems.setShowGrid(true);
            tablaItems.setGridColor(UITheme.BORDE);
            JScrollPane scrollItems = new JScrollPane(tablaItems);
            scrollItems.setPreferredSize(new Dimension(0, 140));
            content.add(scrollItems, BorderLayout.CENTER);

            JPanel panelSur = new JPanel(new BorderLayout(0, 8));
            panelSur.setOpaque(false);

            JPanel panelTotales = new JPanel(new GridBagLayout());
            panelTotales.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(4, 0, 4, 0);

            JLabel lblTotal = new JLabel("Total: $" + String.format("%.2f", totalOrden));
            lblTotal.setFont(UITheme.FONT_SUBTITULO);
            panelTotales.add(lblTotal, gbc);
            gbc.gridy = 1;
            JLabel lblPagado = new JLabel("Valor pagado: $" + String.format("%.2f", orden.getValorPagado()));
            lblPagado.setFont(UITheme.FONT_NORMAL);
            panelTotales.add(lblPagado, gbc);
            gbc.gridy = 2;
            JLabel lblCambio = new JLabel("Cambio: $" + String.format("%.2f", orden.getCambio()));
            lblCambio.setFont(UITheme.FONT_NORMAL);
            panelTotales.add(lblCambio, gbc);

            panelSur.add(panelTotales, BorderLayout.CENTER);

            JButton btnCerrar = UIComponents.crearBotonLink("Cerrar");
            btnCerrar.addActionListener(e -> dispose());
            JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBtn.setOpaque(false);
            panelBtn.add(btnCerrar);
            panelSur.add(panelBtn, BorderLayout.SOUTH);

            content.add(panelSur, BorderLayout.SOUTH);
        }

        private void agregarFilaInfo(JPanel p, String etiqueta, String valor) {
            JLabel lbl = new JLabel(etiqueta);
            lbl.setFont(UITheme.FONT_ETIQUETA);
            lbl.setForeground(UITheme.TEXTO_SECUNDARIO);
            p.add(lbl);
            JLabel val = new JLabel(valor);
            val.setFont(UITheme.FONT_NORMAL);
            val.setToolTipText(valor != null && valor.length() > 40 ? valor : null);
            p.add(val);
        }
    }
}
