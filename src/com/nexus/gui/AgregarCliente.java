package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteYaExiste;
import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.enums.TipoDocumento;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pantalla para agregar un nuevo cliente.
 */
public class AgregarCliente extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JTextField txtNumDoc;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;

    public AgregarCliente() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Agregar Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 480, 380);
        setResizable(false);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarClientes().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 8));
        panelForm.setPreferredSize(new Dimension(420, 220));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Tipo documento: "));
        cmbTipoDoc = new JComboBox<>(TipoDocumento.values());
        cmbTipoDoc.setPreferredSize(new Dimension(120, 25));
        row1.add(cmbTipoDoc);
        panelForm.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Número documento: "));
        txtNumDoc = new JTextField(15);
        txtNumDoc.setForeground(Color.BLACK);
        txtNumDoc.setBackground(Color.WHITE);
        txtNumDoc.setCaretColor(Color.BLACK);
        row2.add(txtNumDoc);
        panelForm.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Nombre: "));
        txtNombre = new JTextField(20);
        txtNombre.setForeground(Color.BLACK);
        txtNombre.setBackground(Color.WHITE);
        txtNombre.setCaretColor(Color.BLACK);
        row3.add(txtNombre);
        panelForm.add(row3);

        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row4.add(new JLabel("Apellido: "));
        txtApellido = new JTextField(20);
        txtApellido.setForeground(Color.BLACK);
        txtApellido.setBackground(Color.WHITE);
        txtApellido.setCaretColor(Color.BLACK);
        row4.add(txtApellido);
        panelForm.add(row4);

        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row5.add(new JLabel("Email: "));
        txtEmail = new JTextField(25);
        txtEmail.setForeground(Color.BLACK);
        txtEmail.setBackground(Color.WHITE);
        txtEmail.setCaretColor(Color.BLACK);
        row5.add(txtEmail);
        panelForm.add(row5);

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar Cliente");
        btnAgregar.addActionListener(e -> agregarCliente());
        panelBotones.add(btnAgregar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void agregarCliente() {
        String numDoc = txtNumDoc.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();

        if (numDoc == null || numDoc.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de documento.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }
        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        if (apellido == null || apellido.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el apellido.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtApellido.requestFocus();
            return;
        }
        if (email == null || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingrese el email.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        numDoc = numDoc.trim();
        nombre = nombre.trim();
        apellido = apellido.trim();
        email = email.trim();

        if (email.contains(" ") || email.contains("\t")) {
            JOptionPane.showMessageDialog(this, "El email no puede contener espacios.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "El formato del email es inválido. Debe ser: nombre@dominio.ext (ej: usuario@correo.com)", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!numDoc.matches("\\d{1,10}")) {
            JOptionPane.showMessageDialog(this, "El documento debe ser numérico y tener máximo 10 dígitos.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            txtNumDoc.requestFocus();
            return;
        }

        TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();

        try {
            controlador.addCliente(tipoDoc, numDoc, nombre, apellido, email);
            JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarClientes().setVisible(true);
            dispose();
        } catch (EClienteYaExiste ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cliente ya existe", JOptionPane.ERROR_MESSAGE);
            txtNumDoc.requestFocus();
        } catch (EParametroNulo | EFormatoInvalido ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
