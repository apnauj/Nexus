package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EUsuarioNoEncontrado;
import com.nexus.model.entities.Usuario;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Pantalla para eliminar un usuario.
 */
public class EliminarUsuario extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private JComboBox<String> cmbUsuario;

    public EliminarUsuario() {
        controlador = StoreController.getInstance();

        if (controlador.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa.", "Error", JOptionPane.ERROR_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        setTitle("Nexus Store - Eliminar Usuario");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 450, 200);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> {
            new GestionarUsuarios().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 1, 0, 10));
        panelForm.setPreferredSize(new Dimension(380, 60));

        Usuario[] usuarios = controlador.getUsuarios();
        Usuario actual = controlador.getCurrentUser();
        if (usuarios.length == 0) {
            panelForm.add(new JLabel("No hay usuarios registrados."));
        } else {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Usuario a eliminar: "));
            String[] nombres = new String[usuarios.length];
            for (int i = 0; i < usuarios.length; i++) {
                nombres[i] = usuarios[i].getUsername();
            }
            cmbUsuario = new JComboBox<>(nombres);
            cmbUsuario.setPreferredSize(new Dimension(200, 25));
            row.add(cmbUsuario);
            panelForm.add(row);
        }

        contentPane.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEliminar = new JButton("Eliminar Usuario");
        btnEliminar.addActionListener(e -> eliminarUsuario());
        panelBotones.add(btnEliminar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void eliminarUsuario() {
        if (controlador.getUsuarios().length == 0) {
            JOptionPane.showMessageDialog(this, "No hay usuarios para eliminar.", "Sin usuarios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) cmbUsuario.getSelectedItem();
        if (username == null || username.isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.equalsIgnoreCase(controlador.getCurrentUser().getUsername())) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propio usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar el usuario administrador.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar al usuario '" + username + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controlador.deleteUsuario(username);
            JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarUsuarios().setVisible(true);
            dispose();
        } catch (EUsuarioNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Usuario no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (EParametroNulo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
