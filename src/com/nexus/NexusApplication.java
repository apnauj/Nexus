package com.nexus;

import com.nexus.gui.Login;
import com.nexus.controller.StoreController;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Punto de entrada único de la aplicación Nexus Store.
 * Inicializa el guardado al cerrar y muestra la pantalla de Login.
 */
public class NexusApplication {

    /**
     * Configura la ventana para guardar datos y salir al cerrar.
     * Usa DO_NOTHING_ON_CLOSE + guardar + System.exit(0) para garantizar
     * que el guardado ocurra antes de terminar (evita race con EXIT_ON_CLOSE).
     */
    public static void addGuardarAlCerrar(JFrame frame, StoreController controlador) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    controlador.guardarFicheros();
                } catch (Exception ex) {
                    System.err.println("Error al guardar al cerrar: " + ex.getMessage());
                }
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        // Inicializar el controlador ANTES de la GUI para que el singleton exista
        // cuando se cierre la ventana (evita init perezosa durante el cierre)
        StoreController ctrl = StoreController.getInstance();

        EventQueue.invokeLater(() -> {
            aplicarLookAndFeel();
            try {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        String[] fallidos = ctrl.guardarFicheros();
                        if (fallidos != null && fallidos.length > 0) {
                            System.err.println("Archivos no guardados: " + String.join(", ", fallidos));
                        }
                    } catch (Exception e) {
                        System.err.println("Error al guardar: " + e.getMessage());
                    }
                }));

                Login frame = new Login();
                addGuardarAlCerrar(frame, ctrl);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al iniciar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            // Usar look and feel por defecto del sistema
        }
    }
}
