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

public class GestionarClientes extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GestionarClientes frame = new GestionarClientes();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GestionarClientes() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 615, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout());

		setContentPane(contentPane);

		// Panel superior
		JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
		contentPane.add(panelSuperior, BorderLayout.NORTH);

		JButton regresar = new JButton("Regresar");
		panelSuperior.add(regresar);

		regresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Store_Controller store= new Store_Controller();
				store.setVisible(true);
				dispose(); // cierra esta ventana
			}
		});

		// Panel central con botones
		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new GridLayout(2, 1, 10, 10));
		contentPane.add(panelBotones, BorderLayout.CENTER);

		JButton deleteCliente = new JButton("Eliminar Cliente");
		panelBotones.add(deleteCliente);

		deleteCliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EliminarCliente cliente = new EliminarCliente();
				cliente.setVisible(true);
			}
		});

		JButton addCliente = new JButton("Agregar Cliente");
		panelBotones.add(addCliente);

		addCliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgregarCliente cliente = new AgregarCliente();
				cliente.setVisible(true);
			}
		});
	}
}