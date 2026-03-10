package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EUsuarioYaExiste;
import com.nexus.model.enums.Rol;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Pantalla para agregar un nuevo usuario.
 * Contraseña: 8-16 caracteres, al menos un número y una mayúscula.
 */
public class AgregarUsuario extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<Rol> cmbRol;

    public AgregarUsuario() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Agregar Usuario");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 520, 420);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(e -> {
            new GestionarUsuarios().setVisible(true);
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
        panelForm.add(new JLabel("Usuario:"), gbc);
        txtUsername = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtUsername, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Contraseña:"), gbc);
        txtPassword = UIComponents.crearCampoPassword(280);
        txtPassword.setToolTipText("8-16 caracteres, al menos 1 número y 1 mayúscula");
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panelForm.add(txtPassword, gbc);
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel hint = new JLabel("(8-16 caracteres, 1 número, 1 mayúscula)");
        hint.setFont(UITheme.FONT_ETIQUETA);
        hint.setForeground(UITheme.TEXTO_SECUNDARIO);
        panelForm.add(hint, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Rol:"), gbc);
        cmbRol = new JComboBox<>(Rol.values());
        cmbRol.setPreferredSize(new Dimension(140, 30));
        gbc.gridx = 1;
        panelForm.add(cmbRol, gbc);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnAgregar = UIComponents.crearBotonPrincipal("Agregar Usuario");
        btnAgregar.addActionListener(e -> agregarUsuario());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void agregarUsuario() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username == null || username.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre de usuario.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if(username.trim().contains(" ")){
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede contener espacios", "Nombre invalido", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (password == null || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese la contraseña.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        if (!password.matches("^(?=.*[0-9])(?=.*[A-Z]).{8,16}$")) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener 8-16 caracteres, al menos un número y una mayúscula.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        Rol rol = (Rol) cmbRol.getSelectedItem();

        try {
            controlador.addUsuario(username.trim(), password, rol);
            JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarUsuarios().setVisible(true);
            dispose();
        } catch (EUsuarioYaExiste ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Usuario ya existe", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
        } catch (EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
