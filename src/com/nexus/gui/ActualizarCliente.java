package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EClienteNoEncontrado;
import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.model.entities.Cliente;
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static com.nexus.NexusApplication.addGuardarAlCerrar;

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


    public ActualizarCliente(TipoDocumento tipoDoc, String numDoc) {
        controlador = StoreController.getInstance();
        setTitle("Nexus Store - Actualizar Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 540, 480);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel contentPane = new JPanel(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(e -> {
            new GestionarClientes().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = UIComponents.crearPanelTarjeta();
        panelForm.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y;
        panelForm.add(new JLabel("Tipo documento:"), gbc);
        cmbTipoDoc = new JComboBox<>(TipoDocumento.values());
        cmbTipoDoc.setPreferredSize(new Dimension(140, 30));
        gbc.gridx = 1; gbc.weightx = 0;
        panelForm.add(cmbTipoDoc, gbc);
        gbc.weightx = 1.0; y++;

        gbc.gridx = 0; gbc.gridy = y;
        panelForm.add(new JLabel("Número documento:"), gbc);
        txtNumDoc = UIComponents.crearCampoTexto(200);
        gbc.gridx = 1;
        panelForm.add(txtNumDoc, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panelForm.add(new JLabel("Nuevo nombre:"), gbc);
        txtNombre = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1;
        panelForm.add(txtNombre, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panelForm.add(new JLabel("Nuevo apellido:"), gbc);
        txtApellido = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1;
        panelForm.add(txtApellido, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panelForm.add(new JLabel("Nuevo email:"), gbc);
        txtEmail = UIComponents.crearCampoTexto(280);
        gbc.gridx = 1;
        panelForm.add(txtEmail, gbc);

        if (tipoDoc != null && numDoc != null && !numDoc.isBlank()) {
            cmbTipoDoc.setSelectedItem(tipoDoc);
            txtNumDoc.setText(numDoc.trim());
            txtNumDoc.setEditable(false);
            cmbTipoDoc.setEnabled(false);
            try {
                Cliente cliente = controlador.searchCliente(tipoDoc, numDoc.trim());
                txtNombre.setText(cliente.getNombre() != null ? cliente.getNombre() : "");
                txtApellido.setText(cliente.getApellido() != null ? cliente.getApellido() : "");
                txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
            } catch (EClienteNoEncontrado | EParametroNulo e) {
                // No prellenar si no se encuentra
            }
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnActualizar = UIComponents.crearBotonPrincipal("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        panelBotones.add(btnActualizar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    //TODO: ENTENDER ESTE MÉTODO
    private void actualizar() {
        //Obtiene el texto de número de documento
        String numDoc = txtNumDoc.getText();
        //Si esta vacio no hace nada (sale del método) y hace un warning de que el documento es obligatorio. Esto no es una excepción pero es el primer filtro por parte de la interfaz
        if (numDoc == null || numDoc.isBlank()) {
            JOptionPane.showMessageDialog(this, "El número de documento es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //Le quita los espacios al numero de documento
        numDoc = numDoc.trim();
        //Si el número de documento no es un número o tiene más de 10 digitos se hace un warning justo como en el bloque de código anterior.
        if (!numDoc.matches("\\d{1,10}")) {
            JOptionPane.showMessageDialog(this, "El documento debe ser numérico (máximo 10 dígitos).", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Se obtiene el tipo de documento
        TipoDocumento tipoDoc = (TipoDocumento) cmbTipoDoc.getSelectedItem();
        //Se obtiene el nombre
        String nombre = txtNombre.getText();
        //Se obtiene el apellido
        String apellido = txtApellido.getText();
        //Se obtiene el email
        String email = txtEmail.getText();

        // Advierte si el nombre esta vacio, como con el número de documento. No es excepción pero es la primera capa de verificación
        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Advierte si el apellido esta vacio, como con el número de documento. No es excepción pero es la primera capa de verificación
        if (apellido == null || apellido.isBlank()) {
            JOptionPane.showMessageDialog(this, "El apellido es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Advierte si el email esta vacio, como con el número de documento. No es excepción pero es la primera capa de verificación
        if (email == null || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "El email es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String emailTrimmed = email.trim();
        if (emailTrimmed.contains(" ") || emailTrimmed.contains("\t")) {
            JOptionPane.showMessageDialog(this, "El email no puede contener espacios.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!emailTrimmed.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "El formato del email es inválido. Debe ser: nombre@dominio.ext (ej: usuario@correo.com)", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            controlador.actualizarCliente(tipoDoc, numDoc, nombre.trim(), apellido.trim(), emailTrimmed);
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
