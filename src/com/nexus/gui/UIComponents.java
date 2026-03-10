package com.nexus.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componentes de UI reutilizables con la estética de Nexus Store.
 * Centraliza botones, campos y paneles para mantener consistencia visual.
 */
public final class UIComponents {

    private UIComponents() {}

    /** Color para botón "Regresar" / "Cerrar sesión" estilo link */
    private static final Color COLOR_LINK = new Color(100, 100, 100);
    private static final Color COLOR_LINK_HOVER = UITheme.COLOR_PRINCIPAL;

    /**
     * Crea un botón principal (acción primaria) - estilo verde como en Login.
     */
    public static JButton crearBotonPrincipal(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.COLOR_PRINCIPAL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(130, 32));
        return boton;
    }

    /**
     * Crea un botón estilo tarjeta (menú) - fondo blanco, bordes redondeados.
     */
    public static JButton crearBotonMenu(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        boton.setPreferredSize(new Dimension(280, 38));
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Crea un botón "Regresar" estilo link (sin fondo, subrayado).
     */
    public static JButton crearBotonRegresar() {
        return crearBotonLink("← Regresar");
    }

    /**
     * Crea un botón estilo link con texto personalizable.
     */
    public static JButton crearBotonLink(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        boton.setForeground(COLOR_LINK);
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINK));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Crea un botón "Eliminar" o destructivo - estilo rojo sutil.
     */
    public static JButton crearBotonEliminar(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(new Color(180, 50, 50));
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 50, 50)));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Aplica el borde estándar a un JTextField o JPasswordField.
     */
    public static void aplicarBordeCampo(JComponent campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
    }

    /**
     * Crea un JTextField con ancho mínimo en píxeles para evitar campos cortados.
     * @param anchoPixeles ancho preferido (ej: 280 para campos de texto largos)
     */
    public static JTextField crearCampoTexto(int anchoPixeles) {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(Math.max(anchoPixeles, 180), 30));
        t.setMinimumSize(new Dimension(150, 28));
        aplicarBordeCampo(t);
        return t;
    }

    /**
     * Crea un JPasswordField con ancho mínimo en píxeles.
     */
    public static JPasswordField crearCampoPassword(int anchoPixeles) {
        JPasswordField t = new JPasswordField();
        t.setPreferredSize(new Dimension(Math.max(anchoPixeles, 180), 30));
        t.setMinimumSize(new Dimension(150, 28));
        aplicarBordeCampo(t);
        return t;
    }

    /**
     * Crea un panel tipo tarjeta (fondo blanco, borde sutil).
     */
    public static JPanel crearPanelTarjeta() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        return p;
    }
}
