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
				MenuPrincipalFrame store= new MenuPrincipalFrame();
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
				GestionarOrdenes.this.dispose();
			}
		});
		panelBotones.add(AddOrden);

		JButton removeOrdenItem = new JButton("Eliminar Item de la Orden");
		removeOrdenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EliminarItemOrden eliminado=new EliminarItemOrden();
				eliminado.setVisible(true);
				GestionarOrdenes.this.dispose();
			}
		});
		
		JButton AddOrdenItem = new JButton("Añadir Item a la orden");
		AddOrdenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgregarItemOrden aio= new AgregarItemOrden();
				aio.setVisible(true);
				GestionarOrdenes.this.dispose();
			}
		});
		panelBotones.add(AddOrdenItem);
		panelBotones.add(removeOrdenItem);
		

		JButton ObtenerOrdenes = new JButton("Obtener historial de ordenes");
		ObtenerOrdenes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListarOrdenes lo = new ListarOrdenes();
				lo.setVisible(true);
				GestionarOrdenes.this.dispose();
			}
		});
		panelBotones.add(ObtenerOrdenes);
	}
}