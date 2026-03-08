package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECredencialesInvalidas;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.entities.LoginService;
import com.nexus.model.entities.Usuario;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pantalla de inicio de sesión.
 * Layout: JFrame con JPanel central, campos de usuario/contraseña y botón Entrar.
 * Al iniciar: carga ficheros y crea admin por defecto si no hay usuarios.
 * Al cerrar: guarda todos los ficheros.
 */
public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    public Login() {
        setTitle("Nexus Store - Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.CENTER;

        // Título
        JLabel lblTitulo = new JLabel("NEXUS STORE");
        lblTitulo.setFont(UITheme.FONT_TITULO);
        lblTitulo.setForeground(UITheme.COLOR_PRINCIPAL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        formPanel.add(lblTitulo, gbc);

        // Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblUsuario, gbc);

        txtUsuario = new JTextField(18);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.gridx = 1;
        formPanel.add(txtUsuario, gbc);

        // Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(18);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        // Botón Entrar
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(UITheme.COLOR_PRINCIPAL);
        btnEntrar.setForeground(java.awt.Color.WHITE);
        btnEntrar.setFocusPainted(false);
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(btnEntrar, gbc);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.anchor = GridBagConstraints.CENTER;
        gbcCenter.weightx = 1.0;
        gbcCenter.weighty = 1.0;
        contentPane.add(formPanel, gbcCenter);

        btnEntrar.addActionListener(this::onEntrar);
        txtPassword.addActionListener(this::onEntrar);
    }

    private void onEntrar(ActionEvent e) {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        try {
            StoreController ctrl = StoreController.getInstance();
            Usuario u = LoginService.login(usuario, password, ctrl.getUsuarios());
            ctrl.setCurrentUser(u);

            MenuPrincipalFrame menu = new MenuPrincipalFrame();
            menu.setVisible(true);
            dispose();
        } catch (EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
        } catch (ECredencialesInvalidas ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Credenciales inválidas", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
