package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.model.enums.Rol;

import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

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
		setBounds(100, 100, 450, 280);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);

		JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton regresar = new JButton("Regresar");
		regresar.addActionListener(e -> {
			new MenuPrincipalFrame().setVisible(true);
			dispose();
		});
		panelSuperior.add(regresar);
		contentPane.add(panelSuperior, BorderLayout.NORTH);

		JPanel panelBotones = new JPanel(new GridLayout(2, 1, 10, 10));
		contentPane.add(panelBotones, BorderLayout.CENTER);

		JButton btnAgregar = new JButton("Agregar Usuario");
		btnAgregar.addActionListener(e -> {
			AgregarUsuario usuario = new AgregarUsuario();
			usuario.setVisible(true);
			dispose();
		});
		panelBotones.add(btnAgregar);

		JButton btnEliminar = new JButton("Eliminar Usuario");
		btnEliminar.addActionListener(e -> {
			EliminarUsuario eUsuario = new EliminarUsuario();
			eUsuario.setVisible(true);
			dispose();
		});
		panelBotones.add(btnEliminar);
	}
}
