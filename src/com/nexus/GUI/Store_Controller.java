package com.nexus.GUI;
import com.nexus.GUI.*;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.nexus.controller.StoreController;

import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

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
		
		
		JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(centerPanel);
		JLabel Titulo = new JLabel("NEXUS STORE");
		Titulo.setForeground(new Color(0, 255, 0));
		GridBagConstraints gbc_Titulo = new GridBagConstraints();
		gbc_Titulo.gridx = 0;
		gbc_Titulo.gridy = 0;
		centerPanel.add(Titulo, gbc_Titulo);
		
		
		
		
		
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
