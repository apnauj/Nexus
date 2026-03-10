package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.model.enums.Rol;

import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GestionarUsuarios extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public GestionarUsuarios() {
		StoreController controlador = StoreController.getInstance();
		if (controlador.getCurrentUser() == null) {
			JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
			new Login().setVisible(true);
			dispose();
			return;
		}
		if (controlador.getCurrentUser().getRol() != Rol.ADMIN) {
			JOptionPane.showMessageDialog(null, "Solo el administrador puede gestionar usuarios.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
			new MenuPrincipalFrame().setVisible(true);
			dispose();
			return;
		}
		setTitle("Nexus Store - Gestionar Usuarios");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, StoreController.getInstance());
		setBounds(100, 100, 450, 320);
		setLocationRelativeTo(null);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBackground(UITheme.FONDO_PANEL);
		contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
		contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
		setContentPane(contentPane);

		JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelSuperior.setOpaque(false);
		JButton regresar = UIComponents.crearBotonRegresar();
		regresar.addActionListener(e -> {
			new MenuPrincipalFrame().setVisible(true);
			dispose();
		});
		panelSuperior.add(regresar);
		contentPane.add(panelSuperior, BorderLayout.NORTH);

		JPanel panelBotones = new JPanel(new GridBagLayout());
		panelBotones.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(6, 0, 6, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		JButton btnAgregar = UIComponents.crearBotonMenu("Agregar Usuario");
		btnAgregar.addActionListener(e -> {
			AgregarUsuario usuario = new AgregarUsuario();
			usuario.setVisible(true);
			dispose();
		});
		panelBotones.add(btnAgregar, gbc);
		gbc.gridy++;

		JPanel panelEliminar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelEliminar.setOpaque(false);
		JButton btnEliminar = UIComponents.crearBotonEliminar("Eliminar Usuario");
		btnEliminar.addActionListener(e -> {
			EliminarUsuario eUsuario = new EliminarUsuario();
			eUsuario.setVisible(true);
			dispose();
		});
		panelEliminar.add(btnEliminar);
		panelBotones.add(panelEliminar, gbc);

		contentPane.add(panelBotones, BorderLayout.CENTER);
	}
}
