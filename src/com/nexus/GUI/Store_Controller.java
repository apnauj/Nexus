package com.nexus.GUI;
import com.nexus.GUI.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.nexus.controller.StoreController;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Store_Controller extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private StoreController controlador;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Store_Controller frame = new Store_Controller();
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
	public Store_Controller() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 678, 427);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton GestionarOrdenes = new JButton("Gestionar Ordenes");
		GestionarOrdenes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GestionarOrdenes go= new GestionarOrdenes();
				go.setVisible(true);
				Store_Controller.this.dispose();
			}
		});
		contentPane.add(GestionarOrdenes);
		
		JButton GestionarProductos = new JButton("Gestionar Productos");
		GestionarProductos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GestionarProductos gp= new GestionarProductos();
				gp.setVisible(true);
				Store_Controller.this.dispose();
			}
		});
		contentPane.add(GestionarProductos);
		
		JButton GestionarClientes = new JButton("Gestionar Clientes");
		GestionarClientes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GestionarClientes gc= new GestionarClientes();
				gc.setVisible(true);
				Store_Controller.this.dispose();
			}
		});
		contentPane.add(GestionarClientes);
		
		JButton GestionarUsuarios = new JButton("Gestionar Usuarios");
		GestionarUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GestionarUsuarios gu= new GestionarUsuarios();
				gu.setVisible(true);
				Store_Controller.this.dispose();
			}
		});
		contentPane.add(GestionarUsuarios);
	}

}
