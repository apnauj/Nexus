package com.nexus.GUI;

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
        setBounds(100, 100, 750, 450);
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

        String[] columnas = { "Nombre", "Categoría", "Tipo", "Precio", "Stock" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
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
            modeloTabla.addRow(new Object[]{
                    nombre,
                    p.getCategoria(),
                    tipo,
                    String.format("%.2f", p.calcularPrecio()),
                    p.getStock()
            });
        }
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
