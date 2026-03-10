package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECredencialesInvalidas;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.service.LoginService;
import com.nexus.model.entities.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    public Login() {
        setTitle("Nexus Store - Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 420, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);

        // Panel tipo tarjeta
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Título
        JLabel lblTitulo = new JLabel("NEXUS STORE");
        lblTitulo.setFont(UITheme.FONT_TITULO);
        lblTitulo.setForeground(UITheme.COLOR_PRINCIPAL);
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(lblTitulo, gbc);

        // Campos (con bordes limpios)
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        formPanel.add(new JLabel("Usuario:"), createGbc(0, 1));
        txtUsuario = new JTextField(15);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        formPanel.add(txtUsuario, createGbc(1, 1));

        formPanel.add(new JLabel("Contraseña:"), createGbc(0, 2));
        txtPassword = new JPasswordField(15);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        formPanel.add(txtPassword, createGbc(1, 2));

        // --- EL BOTÓN REDISEÑADO ---
        JButton btnEntrar = new JButton("Entrar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Color de fondo (puedes usar UITheme.COLOR_PRINCIPAL)
                g2.setColor(UITheme.COLOR_PRINCIPAL);

                // Dibujar el botón redondeado (arcWidth: 15, arcHeight: 15)
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

                // Texto
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        // Propiedades necesarias para que el diseño custom funcione
        btnEntrar.setContentAreaFilled(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setPreferredSize(new Dimension(120, 35));

        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 0, 0, 0);
        formPanel.add(btnEntrar, gbc);

        contentPane.add(formPanel);

        btnEntrar.addActionListener(this::onEntrar);
        txtPassword.addActionListener(this::onEntrar);
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    private void onEntrar(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        try {
            StoreController ctrl = StoreController.getInstance();
            Usuario u = LoginService.login(usuario, password, ctrl.getUsuarios());
            ctrl.setCurrentUser(u);
            new MenuPrincipalFrame().setVisible(true);
            dispose();
        } catch (EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
        } catch (ECredencialesInvalidas ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            txtPassword.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}