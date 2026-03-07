package com.nexus.GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GestionarOrdenes extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GestionarOrdenes frame = new GestionarOrdenes();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GestionarOrdenes() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 615, 400);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());

		setContentPane(contentPane);

		// Panel superior para el botón regresar
		JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton Regresar = new JButton("Regresar");

		Regresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Store_Controller store= new Store_Controller();
				store.setVisible(true);
				GestionarOrdenes.this.dispose(); // cierra esta ventana		
			}
		});

		panelSuperior.add(Regresar);
		contentPane.add(panelSuperior, BorderLayout.NORTH);

		// Panel central para los botones verticales
		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new GridLayout(0, 1, 10, 10));
		contentPane.add(panelBotones, BorderLayout.CENTER);

		JButton AddOrden = new JButton("Añadir Orden");
		AddOrden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgregarOrden ao = new AgregarOrden();
				ao.setVisible(true);
			}
		});
		panelBotones.add(AddOrden);

		JButton EliminarOrdenItem = new JButton("Eliminar Item de la Orden");
		EliminarOrdenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
			}
		});
		
		JButton AddOrdenItem = new JButton("Añadir Item a la orden");
		AddOrdenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddItemOrden aio= new AddItemOrden();
				aio.setVisible(true);
			}
		});
		panelBotones.add(AddOrdenItem);
		panelBotones.add(EliminarOrdenItem);
		

		JButton ObtenerOrdenes = new JButton("Obtener historial de ordenes");
		ObtenerOrdenes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListarOrdenes lo = new ListarOrdenes();
				lo.setVisible(true);
			}
		});
		panelBotones.add(ObtenerOrdenes);
	}
}