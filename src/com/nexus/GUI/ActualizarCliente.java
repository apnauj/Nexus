package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteNoEncontrado;
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
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pantalla para actualizar datos de un cliente existente (nombre, apellido, email).
 */
public class ActualizarCliente extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JTextField txtNumDoc;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;

    public ActualizarCliente() {
        this(null, null);
    }

    public ActualizarCliente(TipoDocumento tipoDoc, String numDoc) {
        controlador = StoreController.getInstance();
        setTitle("Nexus Store - Actualizar Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 500, 320);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarClientes().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 10));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Tipo documento: "));
        cmbTipoDoc = new JComboBox<>(TipoDocumento.values());
        cmbTipoDoc.setPreferredSize(new java.awt.Dimension(120, 25));
        row1.add(cmbTipoDoc);
        panelForm.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Número documento: "));
        txtNumDoc = crearTextField(18);
        row2.add(txtNumDoc);
        panelForm.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Nuevo nombre: "));
        txtNombre = crearTextField(25);
        row3.add(txtNombre);
        panelForm.add(row3);

        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row4.add(new JLabel("Nuevo apellido: "));
        txtApellido = crearTextField(25);
        row4.add(txtApellido);
        panelForm.add(row4);

        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row5.add(new JLabel("Nuevo email: "));
        txtEmail = crearTextField(28);
        row5.add(txtEmail);
        panelForm.add(row5);

        if (tipoDoc != null && numDoc != null && !numDoc.isBlank()) {
            cmbTipoDoc.setSelectedItem(tipoDoc);
            txtNumDoc.setText(numDoc);
            txtNumDoc.setEditable(false);
            cmbTipoDoc.setEnabled(false);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        panelBotones.add(btnActualizar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private JTextField crearTextField(int cols) {
        JTextField t = new JTextField(cols);
        t.setForeground(Color.BLACK);
        t.setBackground(Color.WHITE);
        t.setCaretColor(Color.BLACK);
        return t;
    }

    private void actualizar() {
        String numDoc = txtNumDoc.getText();
        if (numDoc == null || numDoc.isBlank()) {
            JOptionPane.showMessageDialog(this, "El número de documento es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        numDoc = numDoc.trim();
        if (!numDoc.matches("\\d{1,10}")) {
            JOptionPane.showMessageDialog(this, "El documento debe ser numérico (máximo 10 dígitos).", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();

        if ((nombre == null || nombre.isBlank()) && (apellido == null || apellido.isBlank()) && (email == null || email.isBlank())) {
            JOptionPane.showMessageDialog(this, "Ingrese al menos un campo a actualizar (nombre, apellido o email).", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controlador.actualizarCliente(tipoDoc, numDoc,
                    nombre != null && !nombre.isBlank() ? nombre.trim() : null,
                    apellido != null && !apellido.isBlank() ? apellido.trim() : null,
                    email != null && !email.isBlank() ? email.trim() : null);
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (EClienteNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cliente no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (EParametroNulo | EFormatoInvalido ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
