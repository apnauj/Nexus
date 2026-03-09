package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteNoEncontrado;
import com.nexus.model.enums.Rol;
import com.nexus.exceptions.EHistorialOrden;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.entities.Cliente;
import com.nexus.model.enums.TipoDocumento;

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
 * Pantalla para gestionar clientes con tabla de visualización.
 */
public class GestionarClientes extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    public GestionarClientes() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }
        if (controlador.getCurrentUser().getRol() == Rol.GESTOR_INVENTARIO) {
            JOptionPane.showMessageDialog(null, "No tiene permisos para gestionar clientes.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            new MenuPrincipalFrame().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Gestionar Clientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 750, 450);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new MenuPrincipalFrame().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = { "Tipo Doc", "Número Doc", "Nombre", "Apellido", "Email" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollTabla = new JScrollPane(tablaClientes);
        contentPane.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 5, 5));
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        JButton btnAgregar = new JButton("Agregar Cliente");
        btnAgregar.addActionListener(e -> {
            new AgregarCliente().setVisible(true);
            dispose();
        });
        panelBotones.add(btnAgregar);

        JButton btnEliminar = new JButton("Eliminar Cliente");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        panelBotones.add(btnEliminar);

        JButton btnActualizar = new JButton("Actualizar Cliente");
        btnActualizar.addActionListener(e -> actualizarSeleccionado());
        panelBotones.add(btnActualizar);

        actualizarTabla();
    }

    //Poner atención a este
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Cliente c : controlador.getClientes()) {
            modeloTabla.addRow(new Object[]{
                    c.getTipoDoc().toString(),
                    c.getNumDoc(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getEmail()
            });
        }
    }

    private Cliente obtenerClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String tipoDocStr = (String) modeloTabla.getValueAt(fila, 0);
        String numDoc = (String) modeloTabla.getValueAt(fila, 1);
        TipoDocumento tipoDoc = TipoDocumento.valueOf(tipoDocStr);
        for (Cliente c : controlador.getClientes()) {
            if (c.getTipoDoc() == tipoDoc && c.getNumDoc().equals(numDoc)) {
                return c;
            }
        }
        return null;
    }

    private void eliminarSeleccionado() {
        Cliente c = obtenerClienteSeleccionado();
        if (c == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar al cliente " + c.getNombre() + " " + c.getApellido() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controlador.deleteCliente(c.getTipoDoc(), c.getNumDoc());
            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            actualizarTabla();
        } catch (EClienteNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cliente no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (EHistorialOrden ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se puede eliminar", JOptionPane.ERROR_MESSAGE);
        } catch (EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarSeleccionado() {
        Cliente c = obtenerClienteSeleccionado();
        if (c == null) return;

        ActualizarCliente ac = new ActualizarCliente(c.getTipoDoc(), c.getNumDoc());
        ac.setVisible(true);
        dispose();
    }
}
