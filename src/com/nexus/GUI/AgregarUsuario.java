package com.nexus.GUI;

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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

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
        setBounds(100, 100, 480, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarUsuarios().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 10));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Usuario: "));
        txtUsername = crearTextField(25);
        row1.add(txtUsername);
        panelForm.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Contraseña: "));
        txtPassword = new JPasswordField(25);
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setCaretColor(Color.BLACK);
        txtPassword.setToolTipText("8-16 caracteres, al menos 1 número y 1 mayúscula");
        row2.add(txtPassword);
        panelForm.add(row2);

        JPanel rowHint = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowHint.add(new JLabel("(8-16 caracteres, 1 número, 1 mayúscula)"));
        panelForm.add(rowHint);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Rol: "));
        cmbRol = new JComboBox<>(Rol.values());
        row3.add(cmbRol);
        panelForm.add(row3);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.addActionListener(e -> agregarUsuario());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private JTextField crearTextField(int cols) {
        JTextField t = new JTextField(cols);
        t.setForeground(Color.BLACK);
        t.setBackground(Color.WHITE);
        t.setCaretColor(Color.BLACK);
        return t;
    }

    private void agregarUsuario() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username == null || username.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre de usuario.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
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
