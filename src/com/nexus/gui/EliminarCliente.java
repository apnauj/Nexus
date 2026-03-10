package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteNoEncontrado;
import com.nexus.exceptions.EHistorialOrden;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.enums.TipoDocumento;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pantalla para eliminar un cliente.
 */
public class EliminarCliente extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JTextField txtNumDoc;

    public EliminarCliente() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Eliminar Cliente");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 500, 300);
        setResizable(true);

        JPanel contentPane = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(e -> {
            new GestionarClientes().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = UIComponents.crearPanelTarjeta();
        panelForm.setLayout(new GridLayout(0, 1, 0, 10));
        panelForm.setPreferredSize(new Dimension(380, 80));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Tipo documento: "));
        cmbTipoDoc = new JComboBox<>(TipoDocumento.values());
        cmbTipoDoc.setPreferredSize(new Dimension(140, 30));
        row1.add(cmbTipoDoc);
        panelForm.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Número documento: "));
        txtNumDoc = new JTextField(15);
        UIComponents.aplicarBordeCampo(txtNumDoc);
        row2.add(txtNumDoc);
        panelForm.add(row2);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnEliminar = UIComponents.crearBotonEliminar("Eliminar Cliente");
        btnEliminar.addActionListener(e -> eliminarCliente());
        panelBotones.add(btnEliminar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void eliminarCliente() {
        String numDoc = txtNumDoc.getText();
        if (numDoc == null || numDoc.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de documento.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }
        numDoc = numDoc.trim();

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();

        try {
            controlador.deleteCliente(tipoDoc, numDoc);
            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarClientes().setVisible(true);
            dispose();
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
}
