package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteYaExiste;
import com.nexus.exceptions.EFormatoInvalido;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Pantalla para agregar un nuevo cliente.
 */
public class AgregarCliente extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JTextField txtNumDoc;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;

    public AgregarCliente() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Agregar Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 520, 480);
        setResizable(true);
        setLocationRelativeTo(null);

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
        panelForm.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Tipo documento:"), gbc);
        cmbTipoDoc = new JComboBox<>(TipoDocumento.values());
        cmbTipoDoc.setPreferredSize(new Dimension(140, 30));
        gbc.gridx = 1;
        panelForm.add(cmbTipoDoc, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Número documento:"), gbc);
        txtNumDoc = UIComponents.crearCampoTexto(200);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtNumDoc, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Nombre:"), gbc);
        txtNombre = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtNombre, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Apellido:"), gbc);
        txtApellido = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtApellido, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Email:"), gbc);
        txtEmail = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtEmail, gbc);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnAgregar = UIComponents.crearBotonPrincipal("Agregar Cliente");
        btnAgregar.addActionListener(e -> agregarCliente());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void agregarCliente() {
        String numDoc = txtNumDoc.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();

        if (numDoc == null || numDoc.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de documento.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }
        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        if (apellido == null || apellido.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el apellido.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtApellido.requestFocus();
            return;
        }
        if (email == null || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el email.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        numDoc = numDoc.trim();
        nombre = nombre.trim();
        apellido = apellido.trim();
        email = email.trim();

        if (email.contains(" ") || email.contains("\t")) {
            JOptionPane.showMessageDialog(this, "El email no puede contener espacios.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "El formato del email es inválido. Debe ser: nombre@dominio.ext (ej: usuario@correo.com)", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!numDoc.matches("\\d{1,10}")) {
            JOptionPane.showMessageDialog(this, "El documento debe ser numérico y tener máximo 10 dígitos.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }

        TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();

        try {
            controlador.addCliente(tipoDoc, numDoc, nombre, apellido, email);
            JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarClientes().setVisible(true);
            dispose();
        } catch (EClienteYaExiste ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cliente ya existe", JOptionPane.ERROR_MESSAGE);
            txtNumDoc.requestFocus();
        } catch (EParametroNulo | EFormatoInvalido ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
