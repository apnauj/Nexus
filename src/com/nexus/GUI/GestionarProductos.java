package com.nexus.GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GestionarProductos extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GestionarProductos frame = new GestionarProductos();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GestionarProductos() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

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
				GestionarProductos.this.dispose();
			}
		});

		// Panel central
		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new GridLayout(3, 1, 10, 10));
		contentPane.add(panelBotones, BorderLayout.CENTER);

		JButton AddHardware = new JButton("Añadir Hardware");
		panelBotones.add(AddHardware);

		AddHardware.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgregarHardware hardware = new AgregarHardware();
				hardware.setVisible(true);
			}
		});

		JButton addVideoJuegos = new JButton("Añadir VideoJuegos");
		panelBotones.add(addVideoJuegos);

		addVideoJuegos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgregarVideoJuegos videoJuegos = new AgregarVideoJuegos();
				videoJuegos.setVisible(true);
			}
		});

		JButton removeProducto = new JButton("Eliminar Producto");
		panelBotones.add(removeProducto);

		removeProducto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EliminarProducto producto = new EliminarProducto();
				producto.setVisible(true);
			}
		});
	}
}