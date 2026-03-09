package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteNoEncontrado;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.enums.MetodoPago;
import com.nexus.model.enums.TipoDocumento;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;

/**
 * Pantalla para crear una nueva orden asignando un cliente.
 * El cliente debe existir previamente (Tipo Doc + Número Doc).
 * La orden queda asociada al cliente seleccionado.
 */
public class AgregarOrden extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private StoreController controlador;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JTextField txtNumDoc;
    private JComboBox<MetodoPago> cmbMetodoPago;

    public AgregarOrden() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Añadir Orden (Asignar Cliente)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 450, 280);
        setResizable(false);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        // Panel superior: Regresar
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarOrdenes().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        // Panel central: formulario de asignación de cliente
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblInfo = new JLabel("La orden debe asignarse a un cliente existente. Ingrese los datos del cliente:");
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(lblInfo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Tipo de documento:"), gbc);
        cmbTipoDoc = new JComboBox<>(TipoDocumento.values());
        gbc.gridx = 1;
        panelForm.add(cmbTipoDoc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelForm.add(new JLabel("Número de documento:"), gbc);
        txtNumDoc = new JTextField(15);
        txtNumDoc.setForeground(Color.BLACK);
        txtNumDoc.setBackground(Color.WHITE);
        txtNumDoc.setCaretColor(Color.BLACK);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        panelForm.add(txtNumDoc, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelForm.add(new JLabel("Método de pago:"), gbc);
        cmbMetodoPago = new JComboBox<>(MetodoPago.values());
        gbc.gridx = 1;
        panelForm.add(cmbMetodoPago, gbc);

        contentPane.add(panelForm, BorderLayout.CENTER);

        // Panel inferior: Crear orden
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCrearOrden = new JButton("Crear Orden");
        btnCrearOrden.addActionListener(e -> crearOrden());
        panelBotones.add(btnCrearOrden);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void crearOrden() {
        String numDoc = txtNumDoc.getText();
        if (numDoc == null || numDoc.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de documento del cliente.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }

        numDoc = numDoc.trim();
        if (!numDoc.matches("\\d{1,10}")) {
            JOptionPane.showMessageDialog(this, "El documento debe ser numérico (máximo 10 dígitos).",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }

        TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();
        MetodoPago metodoPago = (MetodoPago) cmbMetodoPago.getSelectedItem();

        try {
            UUID idOrden = controlador.addOrden(tipoDoc, numDoc, metodoPago);
            String msg = "Orden creada correctamente.\nCliente asignado.\n\n"
                    + "ID orden: " + idOrden.toString().substring(0, 8) + "...\n\n"
                    + "Puede añadir productos a la orden desde 'Añadir Item a la Orden'.";
            JOptionPane.showMessageDialog(this, msg, "Orden creada", JOptionPane.INFORMATION_MESSAGE);
            new GestionarOrdenes().setVisible(true);
            dispose();
        } catch (EClienteNoEncontrado ex) {
            JOptionPane.showMessageDialog(this,
                    "El cliente con " + tipoDoc + " " + numDoc + " no existe.\n\n"
                            + "Debe agregar el cliente primero desde 'Gestionar Clientes'.",
                    "Cliente no encontrado", JOptionPane.ERROR_MESSAGE);
            txtNumDoc.requestFocus();
        } catch (EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
