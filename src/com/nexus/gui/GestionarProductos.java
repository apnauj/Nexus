package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.model.entities.Hardware;
import com.nexus.model.entities.Producto;
import com.nexus.model.entities.Videojuego;
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
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, UITheme.VENTANA_TABLA_ANCHO, UITheme.VENTANA_TABLA_ALTO);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
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
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        contentPane.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 5, 5));
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        JButton btnAgregarHardware = new JButton("Añadir Hardware");
        btnAgregarHardware.addActionListener(e -> {
            new AgregarHardware().setVisible(true);
            dispose();
        });
        panelBotones.add(btnAgregarHardware);

        JButton btnAgregarVideojuego = new JButton("Añadir Videojuego");
        btnAgregarVideojuego.addActionListener(e -> {
            new AgregarVideoJuegos().setVisible(true);
            dispose();
        });
        panelBotones.add(btnAgregarVideojuego);

        JButton btnVerDetalle = new JButton("Ver detalle del producto");
        btnVerDetalle.addActionListener(e -> mostrarDetalleProducto());
        panelBotones.add(btnVerDetalle);

        JButton btnEliminar = new JButton("Eliminar Producto");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        panelBotones.add(btnEliminar);

        JButton btnActualizar = new JButton("Actualizar Producto");
        btnActualizar.addActionListener(e -> actualizarSeleccionado());
        panelBotones.add(btnActualizar);

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
        } catch (com.nexus.exceptions.EProductoNoEncontrado ex) {
            return;
        }

        String descripcionStr = p.getDescripcion() != null ? p.getDescripcion() : "-";
        String categoriaStr = p.getCategoria() != null ? p.getCategoria() : "-";
        String tipoStr = p instanceof Hardware ? "Hardware" : "Videojuego";
        String descuentoStr = p.isDescuentoActivo() ? (int) Math.round(p.getDescuento() * 100) + "%" : "Inactivo";

        String msg = "Nombre: " + p.getNombre() + "\n"
                + "Descripción: " + descripcionStr + "\n"
                + "Categoría: " + categoriaStr + "\n"
                + "Tipo: " + tipoStr + "\n"
                + "Precio base: $" + String.format("%.2f", p.getPrecioBase()) + "\n"
                + "Descuento: " + descuentoStr + "\n"
                + "Precio final: $" + String.format("%.2f", p.calcularPrecio()) + "\n"
                + "Stock: " + p.getStock() + "\n"
                + "Garantía: " + p.getTiempoGarantia() + " meses\n";

        if (p instanceof Hardware h) {
            String fabricanteStr = h.getFabricante() != null ? h.getFabricante() : "-";
            msg = msg + "Consumo: " + h.getConsumo() + " W\n"
                    + "Fabricante: " + fabricanteStr + "\n";
        } else if (p instanceof Videojuego v) {
            String desarrolladorStr = v.getDesarrollador() != null ? v.getDesarrollador() : "-";
            String generoStr = v.getGenero() != null ? v.getGenero() : "-";
            String plataformaStr = v.getPlataforma() != null ? v.getPlataforma() : "-";
            msg = msg + "Desarrollador: " + desarrolladorStr + "\n"
                    + "Género: " + generoStr + "\n"
                    + "Multijugador: " + (v.getMultijugador() ? "Sí" : "No") + "\n"
                    + "Plataforma: " + plataformaStr + "\n"
                    + "Tamaño: " + v.getTamano() + " GB\n";
            if (v.getFechaLanzamiento() != null) {
                msg = msg + "Fecha lanzamiento: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(v.getFechaLanzamiento()) + "\n";
            }
        }

        JOptionPane.showMessageDialog(this, msg, "Detalle del producto", JOptionPane.INFORMATION_MESSAGE);
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
}
