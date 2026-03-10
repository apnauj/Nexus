package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.model.enums.Rol;
import com.nexus.model.entities.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Menú principal de la aplicación con estética mejorada.
 */
public class MenuPrincipalFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private StoreController controlador;
    private Usuario usuarioActual;

    public MenuPrincipalFrame() {
        controlador = StoreController.getInstance();
        usuarioActual = controlador.getCurrentUser();

        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa. Redirigiendo al login.",
                    "Sesión inválida", JOptionPane.WARNING_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Panel de Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 550, 500); // Un poco más ancho para las tarjetas
        setResizable(false);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(0, 20));
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(contentPane);

        // --- PANEL SUPERIOR ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("NEXUS STORE");
        lblTitulo.setFont(UITheme.FONT_TITULO);
        lblTitulo.setForeground(UITheme.COLOR_PRINCIPAL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblUsuario = new JLabel("Bienvenido, " + usuarioActual.getUsername() + " [" + usuarioActual.getRol() + "]");
        lblUsuario.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblUsuario.setForeground(UITheme.TEXTO_SECUNDARIO);
        lblUsuario.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(lblTitulo);
        topPanel.add(lblUsuario);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // --- PANEL CENTRAL (BOTONES) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Rol rol = usuarioActual.getRol();

        if (rol == Rol.ADMIN || rol == Rol.EMPLEADO_VENTAS || rol == Rol.GESTOR_INVENTARIO) {
            JButton btn = UIComponents.crearBotonMenu("Gestionar Órdenes");
            btn.addActionListener(e -> abrirGestionarOrdenes());
            centerPanel.add(btn, gbc);
            gbc.gridy++;
        }

        if (rol == Rol.ADMIN || rol == Rol.GESTOR_INVENTARIO) {
            JButton btn = UIComponents.crearBotonMenu("Gestionar Productos");
            btn.addActionListener(e -> abrirGestionarProductos());
            centerPanel.add(btn, gbc);
            gbc.gridy++;

            JButton btnRep = UIComponents.crearBotonMenu("Reporte de Órdenes");
            btnRep.addActionListener(e -> abrirReporteOrdenes());
            centerPanel.add(btnRep, gbc);
            gbc.gridy++;
        }

        if (rol == Rol.ADMIN || rol == Rol.EMPLEADO_VENTAS) {
            JButton btn = UIComponents.crearBotonMenu("Gestionar Clientes");
            btn.addActionListener(e -> abrirGestionarClientes());
            centerPanel.add(btn, gbc);
            gbc.gridy++;
        }

        if (rol == Rol.ADMIN) {
            JButton btn = UIComponents.crearBotonMenu("Gestionar Usuarios");
            btn.addActionListener(e -> abrirGestionarUsuarios());
            centerPanel.add(btn, gbc);
            gbc.gridy++;
        }

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- PANEL INFERIOR ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        JButton btnCerrarSesion = UIComponents.crearBotonEliminar("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        bottomPanel.add(btnCerrarSesion);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- MÉTODOS DE NAVEGACIÓN ---
    private void abrirGestionarOrdenes() {
        new GestionarOrdenes().setVisible(true);
        dispose();
    }

    private void abrirGestionarProductos() {
        new GestionarProductos().setVisible(true);
        dispose();
    }

    private void abrirReporteOrdenes() {
        new ReporteOrdenes().setVisible(true);
        dispose();
    }

    private void abrirGestionarClientes() {
        new GestionarClientes().setVisible(true);
        dispose();
    }

    private void abrirGestionarUsuarios() {
        new GestionarUsuarios().setVisible(true);
        dispose();
    }

    private void cerrarSesion() {
        try {
            controlador.guardarFicheros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
        controlador.logout();
        new Login().setVisible(true);
        dispose();
    }
}