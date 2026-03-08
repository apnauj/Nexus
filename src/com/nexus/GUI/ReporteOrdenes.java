package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.model.entities.Cliente;
import com.nexus.model.entities.Orden;
import com.nexus.model.entities.OrdenItem;
import com.nexus.model.enums.Estado;

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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Pantalla de reporte de órdenes por rango de fechas.
 * Disponible para Administrador y Gestor de inventario.
 */
public class ReporteOrdenes extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JLabel lblTotalVentas;
    private Orden[] ordenesActuales;

    public ReporteOrdenes() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }
        com.nexus.model.enums.Rol rol = controlador.getCurrentUser().getRol();
        if (rol != com.nexus.model.enums.Rol.GESTOR_INVENTARIO && rol != com.nexus.model.enums.Rol.ADMIN) {
            JOptionPane.showMessageDialog(null, "Solo el Administrador o el Gestor de inventario pueden acceder a reportes.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            new MenuPrincipalFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Reporte de Órdenes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 820, 560);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        // Título
        JLabel lblTitulo = new JLabel("Reporte de Órdenes");
        lblTitulo.setFont(UITheme.FONT_TITULO);
        lblTitulo.setForeground(UITheme.COLOR_PRINCIPAL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panelSuperior = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        panelSuperior.setOpaque(false);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.addActionListener(e -> {
            new MenuPrincipalFrame().setVisible(true);
            dispose();
        });
        JPanel panelRegresar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRegresar.setOpaque(false);
        panelRegresar.add(btnRegresar);
        panelSuperior.add(panelRegresar, BorderLayout.WEST);

        // Panel de filtro
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelFiltro.setBackground(Color.WHITE);
        panelFiltro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDE),
                new EmptyBorder(10, 10, 10, 10)));
        panelFiltro.setBackground(Color.WHITE);

        JLabel lblDesde = new JLabel("Desde:");
        lblDesde.setFont(UITheme.FONT_ETIQUETA);
        panelFiltro.add(lblDesde);
        txtFechaDesde = crearTextField(12);
        txtFechaDesde.setToolTipText("Formato: dd/MM/yyyy");
        panelFiltro.add(txtFechaDesde);

        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setFont(UITheme.FONT_ETIQUETA);
        panelFiltro.add(lblHasta);
        txtFechaHasta = crearTextField(12);
        txtFechaHasta.setToolTipText("Formato: dd/MM/yyyy");
        panelFiltro.add(txtFechaHasta);

        JButton btnGenerar = new JButton("Generar Reporte");
        btnGenerar.setBackground(UITheme.COLOR_PRINCIPAL);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generarReporte());
        panelFiltro.add(btnGenerar);

        JPanel panelNorte = new JPanel(new BorderLayout(0, UITheme.ESPACIADO));
        panelNorte.setOpaque(false);
        panelNorte.add(panelSuperior, BorderLayout.NORTH);
        panelNorte.add(panelFiltro, BorderLayout.CENTER);
        contentPane.add(panelNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = { "ID", "Cliente", "Fecha", "Estado", "Total", "Valor Pagado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getTableHeader().setReorderingAllowed(false);
        tablaOrdenes.setRowHeight(28);
        tablaOrdenes.setFont(UITheme.FONT_NORMAL);
        tablaOrdenes.setShowGrid(true);
        tablaOrdenes.setGridColor(UITheme.BORDE);
        tablaOrdenes.setBackground(Color.WHITE);

        JTableHeader header = tablaOrdenes.getTableHeader();
        header.setFont(UITheme.FONT_SUBTITULO);
        header.setBackground(new Color(230, 240, 235));
        header.setForeground(UITheme.TEXTO);
        header.setOpaque(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnas.length; i++) {
            tablaOrdenes.getColumnModel().getColumn(i).setCellRenderer(
                    (i == 3 || i == 4 || i == 5) ? centerRenderer : new DefaultTableCellRenderer());
        }

        JScrollPane scrollTabla = new JScrollPane(tablaOrdenes);
        scrollTabla.setBorder(BorderFactory.createLineBorder(UITheme.BORDE));
        contentPane.add(scrollTabla, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        panelInferior.setOpaque(false);

        JButton btnVerDetalle = new JButton("Ver detalle de orden");
        btnVerDetalle.addActionListener(e -> verDetalleOrden());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setOpaque(false);
        panelBotones.add(btnVerDetalle);
        panelInferior.add(panelBotones, BorderLayout.WEST);

        lblTotalVentas = new JLabel("Total ventas (aprobadas): $0.00");
        lblTotalVentas.setFont(UITheme.FONT_SUBTITULO);
        lblTotalVentas.setForeground(UITheme.COLOR_PRINCIPAL);
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setOpaque(false);
        panelTotal.add(lblTotalVentas);
        panelInferior.add(panelTotal, BorderLayout.EAST);
        contentPane.add(panelInferior, BorderLayout.SOUTH);

        ordenesActuales = new Orden[0];
    }

    private JTextField crearTextField(int cols) {
        JTextField t = new JTextField(cols);
        t.setFont(UITheme.FONT_NORMAL);
        return t;
    }

    private void generarReporte() {
        String desdeStr = txtFechaDesde.getText();
        String hastaStr = txtFechaHasta.getText();
        if (desdeStr == null || desdeStr.isBlank() || hastaStr == null || hastaStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese las fechas de inicio y fin.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate inicio;
        LocalDate fin;
        try {
            inicio = LocalDate.parse(desdeStr.trim(), dtf);
            fin = LocalDate.parse(hastaStr.trim(), dtf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Las fechas deben estar en formato dd/MM/yyyy.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (inicio.isAfter(fin)) {
            JOptionPane.showMessageDialog(this, "La fecha de inicio no puede ser posterior a la fecha fin.", "Rango inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ordenesActuales = controlador.getOrdenesPorFechas(inicio, fin);
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        double totalVentas = 0;
        for (Orden o : ordenesActuales) {
            Cliente c = o.getCliente();
            String clienteStr = (c != null) ? c.getNombre() + " " + c.getApellido() : "-";
            String idStr = o.getIdPedido().toString().substring(0, Math.min(8, o.getIdPedido().toString().length())) + "...";
            double total = o.getTotal();
            if (o.getItems() != null && o.getItems().length > 0 && total == 0) {
                total = o.calcularTotal();
            }
            if (o.getEstado() == Estado.APROBADO) {
                totalVentas += total;
            }
            String valorPagadoStr = (o.getValorPagado() > 0) ? String.format("%.2f", o.getValorPagado()) : "-";
            modeloTabla.addRow(new Object[]{
                    idStr,
                    clienteStr,
                    o.getFecha(),
                    o.getEstado().toString(),
                    String.format("$%.2f", total),
                    valorPagadoStr
            });
        }
        lblTotalVentas.setText("Total ventas (aprobadas): $" + String.format("%.2f", totalVentas));
    }

    private void verDetalleOrden() {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fila >= ordenesActuales.length) return;

        Orden orden = ordenesActuales[fila];
        DetalleOrdenDialog dlg = new DetalleOrdenDialog(this, orden);
        dlg.setVisible(true);
    }

    /**
     * Diálogo modal para mostrar el detalle de una orden con formato visual mejorado.
     */
    private static class DetalleOrdenDialog extends JDialog {
        public DetalleOrdenDialog(Window parent, Orden orden) {
            super(parent, "Detalle de orden", ModalityType.APPLICATION_MODAL);

            setSize(480, 420);
            setLocationRelativeTo(parent);
            setResizable(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel content = new JPanel();
            content.setBackground(UITheme.FONDO_PANEL);
            content.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
            content.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
            setContentPane(content);

            JPanel panelInfo = new JPanel(new GridBagLayout());
            panelInfo.setBackground(Color.WHITE);
            panelInfo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDE),
                    new EmptyBorder(12, 12, 12, 12)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 0, 4, 16);

            Cliente c = orden.getCliente();
            String clienteStr = (c != null) ? c.getNombre() + " " + c.getApellido() : "-";

            agregarFila(panelInfo, gbc, "ID:", orden.getIdPedido().toString());
            agregarFila(panelInfo, gbc, "Cliente:", clienteStr);
            agregarFila(panelInfo, gbc, "Fecha:", orden.getFecha());
            agregarFila(panelInfo, gbc, "Estado:", orden.getEstado().toString());
            agregarFila(panelInfo, gbc, "Método de pago:", orden.getMetodoPago() != null ? orden.getMetodoPago().toString() : "-");

            content.add(panelInfo, BorderLayout.NORTH);

            // Tabla de items
            String[] cols = { "Producto", "Cant.", "P. Unit.", "Subtotal" };
            DefaultTableModel modelItems = new DefaultTableModel(cols, 0) {
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
                            nombreProd,
                            cant,
                            String.format("$%.2f", pUnit),
                            String.format("$%.2f", subtotal)
                    });
                }
            }

            JTable tablaItems = new JTable(modelItems);
            tablaItems.setRowHeight(24);
            tablaItems.setFont(UITheme.FONT_NORMAL);
            tablaItems.getTableHeader().setFont(UITheme.FONT_ETIQUETA);
            tablaItems.setShowGrid(true);
            tablaItems.setGridColor(UITheme.BORDE);
            JScrollPane scrollItems = new JScrollPane(tablaItems);
            scrollItems.setPreferredSize(new Dimension(0, 140));
            content.add(scrollItems, BorderLayout.CENTER);

            // Totales y botón
            JPanel panelSur = new JPanel(new BorderLayout(0, 8));
            panelSur.setOpaque(false);

            JPanel panelTotales = new JPanel(new GridBagLayout());
            panelTotales.setOpaque(false);
            gbc = new GridBagConstraints();
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

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dispose());
            JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBtn.setOpaque(false);
            panelBtn.add(btnCerrar);
            panelSur.add(panelBtn, BorderLayout.SOUTH);

            content.add(panelSur, BorderLayout.SOUTH);
        }

        private void agregarFila(JPanel p, GridBagConstraints gbc, String etiqueta, String valor) {
            JLabel lbl = new JLabel(etiqueta);
            lbl.setFont(UITheme.FONT_ETIQUETA);
            lbl.setForeground(UITheme.TEXTO_SECUNDARIO);
            gbc.gridx = 0;
            gbc.weightx = 0;
            p.add(lbl, gbc);

            JLabel val = new JLabel(valor);
            val.setFont(UITheme.FONT_NORMAL);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            p.add(val, gbc);
            gbc.gridy++;
        }
    }
}
