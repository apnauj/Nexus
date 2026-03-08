package com.nexus.GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GestionarUsuarios extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GestionarUsuarios frame = new GestionarUsuarios();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GestionarUsuarios() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout()); // IMPORTANTE

		setContentPane(contentPane);

		// Panel superior
		JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
		contentPane.add(panelSuperior, BorderLayout.NORTH);

		JButton regresar = new JButton("Regresar");
		panelSuperior.add(regresar);

		regresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuPrincipalFrame store= new MenuPrincipalFrame();
				store.setVisible(true);
				GestionarUsuarios.this.dispose();
			}
		});

		
		// Panel central
				JPanel panelBotones = new JPanel();
				panelBotones.setLayout(new GridLayout(3, 1, 10, 10));
				contentPane.add(panelBotones, BorderLayout.CENTER);
		
		JButton AddUsuario = new JButton("Agregar Usuario");
		panelBotones.add(AddUsuario);
		
		AddUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgregarUsuario usuario=new AgregarUsuario();
				usuario.setVisible(true);
			}
		});
		
		
		JButton deleteUsuario = new JButton("Eliminar Usuario");
		deleteUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EliminarUsuario eUsuario= new EliminarUsuario();
				eUsuario.setVisible(true);
			}
		});
		panelBotones.add(deleteUsuario);
	}

}
