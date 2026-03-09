package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.model.enums.Rol;
import com.nexus.model.entities.Usuario;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Menú principal de la aplicación.
 * Muestra botones según el rol del usuario:
 * - ADMIN: acceso total (Órdenes, Productos, Clientes, Usuarios)
 * - EMPLEADO_VENTAS: solo CRUD Órdenes y CRUD Clientes
 * - GESTOR_INVENTARIO: solo ver Órdenes y CRUD Productos
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

        setTitle("Nexus Store - Menú principal (" + usuarioActual.getUsername() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 500, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, UITheme.ESPACIADO));

        // Panel superior: título y usuario
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        topPanel.setOpaque(false);
        JLabel lblTitulo = new JLabel("NEXUS STORE");
        lblTitulo.setFont(UITheme.FONT_TITULO);
        lblTitulo.setForeground(UITheme.COLOR_PRINCIPAL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(lblTitulo);

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioActual.getUsername() + " (" + usuarioActual.getRol() + ")");
        lblUsuario.setFont(UITheme.FONT_ETIQUETA);
        lblUsuario.setForeground(UITheme.TEXTO_SECUNDARIO);
        lblUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(lblUsuario);

        contentPane.add(topPanel, BorderLayout.NORTH);

        // Panel central: botones según rol
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Rol rol = usuarioActual.getRol();

        // Gestionar Órdenes: ADMIN, EMPLEADO_VENTAS (CRUD), GESTOR_INVENTARIO (solo ver)
        if (rol == Rol.ADMIN || rol == Rol.EMPLEADO_VENTAS || rol == Rol.GESTOR_INVENTARIO) {
            JButton btnOrdenes = new JButton("Gestionar Órdenes");
            btnOrdenes.addActionListener(e -> abrirGestionarOrdenes());
            gbc.gridy = 0;
            centerPanel.add(btnOrdenes, gbc);
        }

        // Gestionar Productos: solo ADMIN y GESTOR_INVENTARIO (CRUD)
        if (rol == Rol.ADMIN || rol == Rol.GESTOR_INVENTARIO) {
            JButton btnProductos = new JButton("Gestionar Productos");
            btnProductos.addActionListener(e -> abrirGestionarProductos());
            gbc.gridy++;
            centerPanel.add(btnProductos, gbc);
        }

        // Reporte de órdenes: ADMIN y GESTOR_INVENTARIO
        if (rol == Rol.ADMIN || rol == Rol.GESTOR_INVENTARIO) {
            JButton btnReporte = new JButton("Reporte de Órdenes");
            btnReporte.addActionListener(e -> abrirReporteOrdenes());
            gbc.gridy++;
            centerPanel.add(btnReporte, gbc);
        }

        // Gestionar Clientes: solo ADMIN y EMPLEADO_VENTAS (CRUD)
        if (rol == Rol.ADMIN || rol == Rol.EMPLEADO_VENTAS) {
            JButton btnClientes = new JButton("Gestionar Clientes");
            btnClientes.addActionListener(e -> abrirGestionarClientes());
            gbc.gridy++;
            centerPanel.add(btnClientes, gbc);
        }

        // Gestionar Usuarios: solo ADMIN
        if (rol == Rol.ADMIN) {
            JButton btnUsuarios = new JButton("Gestionar Usuarios");
            btnUsuarios.addActionListener(e -> abrirGestionarUsuarios());
            gbc.gridy++;
            centerPanel.add(btnUsuarios, gbc);
        }

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // Panel inferior: cerrar sesión
        JPanel bottomPanel = new JPanel();
        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        bottomPanel.add(btnCerrarSesion);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void abrirGestionarOrdenes() {
        try {
            GestionarOrdenes go = new GestionarOrdenes();
            go.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirGestionarProductos() {
        try {
            GestionarProductos gp = new GestionarProductos();
            gp.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirReporteOrdenes() {
        try {
            ReporteOrdenes ro = new ReporteOrdenes();
            ro.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirGestionarClientes() {
        try {
            GestionarClientes gc = new GestionarClientes();
            gc.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirGestionarUsuarios() {
        try {
            GestionarUsuarios gu = new GestionarUsuarios();
            gu.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarSesion() {
        try {
            controlador.guardarFicheros();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        controlador.logout();
        new Login().setVisible(true);
        dispose();
    }
}
