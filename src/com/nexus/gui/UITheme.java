package com.nexus.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes de tema visual para la aplicación Nexus Store.
 * Mantiene consistencia en colores, fuentes y espaciado.
 */
public final class UITheme {

    private UITheme() {}

    /** Color principal de la marca (verde Nexus) */
    public static final Color COLOR_PRINCIPAL = new Color(0, 120, 80);
    /** Color secundario para acentos */
    public static final Color COLOR_SECUNDARIO = new Color(0, 100, 65);
    /** Fondo de paneles */
    public static final Color FONDO_PANEL = new Color(248, 250, 252);
    /** Borde sutil */
    public static final Color BORDE = new Color(220, 225, 230);
    /** Texto principal */
    public static final Color TEXTO = new Color(33, 37, 41);
    /** Texto secundario */
    public static final Color TEXTO_SECUNDARIO = new Color(73, 80, 87);
    /** Éxito / aprobado */
    public static final Color EXITO = new Color(25, 135, 84);
    /** Advertencia */
    public static final Color ADVERTENCIA = new Color(255, 193, 7);
    /** Error */
    public static final Color ERROR = new Color(220, 53, 69);

    /** Fuente título grande */
    public static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 22);
    /** Fuente subtítulo */
    public static final Font FONT_SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    /** Fuente normal */
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    /** Fuente etiqueta */
    public static final Font FONT_ETIQUETA = new Font("SansSerif", Font.PLAIN, 12);
    /** Fuente encabezados de tabla (negrita) */
    public static final Font FONT_ENCABEZADO_TABLA = new Font("SansSerif", Font.BOLD, 13);

    /** Ancho estándar para ventanas de formulario (productos) */
    public static final int VENTANA_FORMULARIO_ANCHO = 520;
    /** Alto estándar para ventanas de formulario (productos) */
    public static final int VENTANA_FORMULARIO_ALTO = 520;
    /** Ancho estándar para ventanas con tabla */
    public static final int VENTANA_TABLA_ANCHO = 750;
    /** Alto estándar para ventanas con tabla (permite ver ~5 filas) */
    public static final int VENTANA_TABLA_ALTO = 520;

    /** Margen estándar */
    public static final int MARGEN = 16;
    /** Espaciado entre elementos */
    public static final int ESPACIADO = 8;
}
