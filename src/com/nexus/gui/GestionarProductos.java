package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.model.entities.Hardware;
import com.nexus.model.entities.Producto;
import com.nexus.model.entities.Videojuego;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static com.nexus.NexusApplication.addGuardarAlCerrar;

/**
 * Pantalla para gestionar productos con tabla de visualización.
 */
public class GestionarProductos extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public GestionarProductos() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }
        if (controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            JOptionPane.showMessageDialog(null, "No tiene permisos para gestionar productos.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            new MenuPrincipalFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Gestionar Productos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, UITheme.VENTANA_TABLA_ANCHO, UITheme.VENTANA_TABLA_ALTO);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(e -> {
            new MenuPrincipalFrame().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = { "Nombre", "Tipo", "Precio Base", "Descuento %", "Precio", "Stock" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.getTableHeader().setFont(UITheme.FONT_ENCABEZADO_TABLA);
        tablaProductos.setRowHeight(24);
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setMinimumSize(new Dimension(400, 150));
        contentPane.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.insets = new Insets(6, 0, 6, 0);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1.0;

        JButton btnAgregarHardware = UIComponents.crearBotonMenu("Añadir Hardware");
        btnAgregarHardware.addActionListener(e -> {
            new AgregarHardware().setVisible(true);
            dispose();
        });
        panelBotones.add(btnAgregarHardware, gbcBtn);
        gbcBtn.gridy++;

        JButton btnAgregarVideojuego = UIComponents.crearBotonMenu("Añadir Videojuego");
        btnAgregarVideojuego.addActionListener(e -> {
            new AgregarVideoJuegos().setVisible(true);
            dispose();
        });
        panelBotones.add(btnAgregarVideojuego, gbcBtn);
        gbcBtn.gridy++;

        JButton btnVerDetalle = UIComponents.crearBotonMenu("Ver detalle del producto");
        btnVerDetalle.addActionListener(e -> mostrarDetalleProducto());
        panelBotones.add(btnVerDetalle, gbcBtn);
        gbcBtn.gridy++;

        JButton btnActualizar = UIComponents.crearBotonMenu("Actualizar Producto");
        btnActualizar.addActionListener(e -> actualizarSeleccionado());
        panelBotones.add(btnActualizar, gbcBtn);
        gbcBtn.gridy++;

        JPanel panelEliminar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEliminar.setOpaque(false);
        JButton btnEliminar = UIComponents.crearBotonEliminar("Eliminar Producto");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        panelEliminar.add(btnEliminar);
        panelBotones.add(panelEliminar, gbcBtn);

        contentPane.add(panelBotones, BorderLayout.SOUTH);

        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Producto p : controlador.getProductos()) {
            String nombre = p.getNombre();
            if (nombre == null || nombre.isBlank()) continue; // omitir productos corruptos
            String tipo = p instanceof Hardware ? "Hardware" : "Videojuego";
            int descuentoPct = p.isDescuentoActivo() ? (int) Math.round(p.getDescuento() * 100) : 0;
            modeloTabla.addRow(new Object[]{
                    nombre,
                    tipo,
                    String.format("%.2f", p.getPrecioBase()),
                    descuentoPct + "%",
                    String.format("%.2f", p.calcularPrecio()),
                    p.getStock()
            });
        }
    }

    private void mostrarDetalleProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        if (nombre == null || nombre.isBlank()) return;
        Producto p;
        try {
            p = controlador.searchProducto(nombre);
        } catch (EProductoNoEncontrado ex) {
            return;
        }

        DetalleProductoDialog dlg = new DetalleProductoDialog(this, p);
        dlg.setVisible(true);
    }

    private String obtenerProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) modeloTabla.getValueAt(fila, 0);
    }

    private void eliminarSeleccionado() {
        String nombre = obtenerProductoSeleccionado();
        if (nombre == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar el producto '" + nombre + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controlador.deleteProducto(nombre);
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            actualizarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarSeleccionado() {
        String nombre = obtenerProductoSeleccionado();
        if (nombre == null) return;

        ActualizarProducto ap = new ActualizarProducto(nombre);
        ap.setVisible(true);
        dispose();
    }

    /**
     * Diálogo modal para mostrar el detalle de un producto con estética consistente.
     */
    private static class DetalleProductoDialog extends JDialog {
        DetalleProductoDialog(Window parent, Producto p) {
            super(parent, "Detalle del producto", Dialog.ModalityType.APPLICATION_MODAL);
            setSize(480, 420);
            setLocationRelativeTo(parent);
            setResizable(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel content = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
            content.setBackground(UITheme.FONDO_PANEL);
            content.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
            setContentPane(content);

            JPanel panelInfo = new JPanel(new GridBagLayout());
            panelInfo.setBackground(java.awt.Color.WHITE);
            panelInfo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDE),
                    new EmptyBorder(16, 20, 16, 20)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 0, 4, 16);

            String descripcionStr = p.getDescripcion() != null ? p.getDescripcion() : "-";
            String categoriaStr = p.getCategoria() != null ? p.getCategoria() : "-";
            String tipoStr = p instanceof Hardware ? "Hardware" : "Videojuego";
            String descuentoStr = p.isDescuentoActivo() ? (int) Math.round(p.getDescuento() * 100) + "%" : "Inactivo";

            agregarFila(panelInfo, gbc, 0, "Nombre:", p.getNombre());
            agregarFila(panelInfo, gbc, 1, "Descripción:", descripcionStr);
            agregarFila(panelInfo, gbc, 2, "Categoría:", categoriaStr);
            agregarFila(panelInfo, gbc, 3, "Tipo:", tipoStr);
            agregarFila(panelInfo, gbc, 4, "Precio base:", "$" + String.format("%.2f", p.getPrecioBase()));
            agregarFila(panelInfo, gbc, 5, "Descuento:", descuentoStr);
            agregarFila(panelInfo, gbc, 6, "Precio final:", "$" + String.format("%.2f", p.calcularPrecio()));
            agregarFila(panelInfo, gbc, 7, "Stock:", String.valueOf(p.getStock()));
            agregarFila(panelInfo, gbc, 8, "Garantía:", p.getTiempoGarantia() + " meses");

            if (p instanceof Hardware h) {
                String fabricanteStr = h.getFabricante() != null ? h.getFabricante() : "-";
                agregarFila(panelInfo, gbc, 9, "Consumo:", h.getConsumo() + " W");
                agregarFila(panelInfo, gbc, 10, "Fabricante:", fabricanteStr);
            } else if (p instanceof Videojuego v) {
                String desarrolladorStr = v.getDesarrollador() != null ? v.getDesarrollador() : "-";
                String generoStr = v.getGenero() != null ? v.getGenero() : "-";
                String plataformaStr = v.getPlataforma() != null ? v.getPlataforma() : "-";
                int row = 9;
                agregarFila(panelInfo, gbc, row++, "Desarrollador:", desarrolladorStr);
                agregarFila(panelInfo, gbc, row++, "Género:", generoStr);
                agregarFila(panelInfo, gbc, row++, "Multijugador:", v.getMultijugador() ? "Sí" : "No");
                agregarFila(panelInfo, gbc, row++, "Plataforma:", plataformaStr);
                agregarFila(panelInfo, gbc, row++, "Tamaño:", v.getTamano() + " GB");
                if (v.getFechaLanzamiento() != null) {
                    agregarFila(panelInfo, gbc, row, "Fecha lanzamiento:",
                            new java.text.SimpleDateFormat("dd/MM/yyyy").format(v.getFechaLanzamiento()));
                }
            }

            content.add(new JScrollPane(panelInfo), BorderLayout.CENTER);

            JButton btnCerrar = UIComponents.crearBotonLink("Cerrar");
            btnCerrar.addActionListener(e -> dispose());
            JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBtn.setOpaque(false);
            panelBtn.add(btnCerrar);
            content.add(panelBtn, BorderLayout.SOUTH);
        }

        private void agregarFila(JPanel p, GridBagConstraints gbc, int y, String etiqueta, String valor) {
            gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
            JLabel lbl = new JLabel(etiqueta);
            lbl.setFont(UITheme.FONT_ETIQUETA);
            lbl.setForeground(UITheme.TEXTO_SECUNDARIO);
            p.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            JLabel val = new JLabel(valor);
            val.setFont(UITheme.FONT_NORMAL);
            val.setToolTipText(valor != null && valor.length() > 50 ? valor : null);
            p.add(val, gbc);
        }
    }
}
