package com.nexus.gui;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EFormatoInvalido;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.Hardware;
import com.nexus.model.entities.Producto;
import com.nexus.model.entities.Videojuego;
import com.nexus.model.enums.Rol;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Pantalla para actualizar un producto existente.
 * Muestra campos específicos según el tipo (Hardware o Videojuego).
 */
public class ActualizarProducto extends JFrame {

    private static final long serialVersionUID = 1L;
    private StoreController controlador;
    private boolean esHardware;
    private Producto productoActual;

    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCategoria;
    private JTextField txtTiempoGarantia;
    private JTextField txtPrecioBase;
    private JTextField txtStock;
    private JTextField txtConsumo;
    private JTextField txtFabricante;
    private JTextField txtDesarrollador;
    private JTextField txtGenero;
    private JCheckBox chkMultijugador;
    private JTextField txtFechaLanzamiento;
    private JTextField txtPlataforma;
    private JTextField txtTamano;
    private JCheckBox chkDescuentoActivo;

    private JPanel panelForm;

    public ActualizarProducto(String nombreProducto) {
        controlador = StoreController.getInstance();
        setTitle("Nexus Store - Actualizar Producto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 560, 600);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(UITheme.FONDO_PANEL);
        contentPane.setBorder(new EmptyBorder(UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN, UITheme.MARGEN));
        contentPane.setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(e -> {
            new GestionarProductos().setVisible(true);
            dispose();
        });
        panelSuperior.add(btnRegresar);
        contentPane.add(panelSuperior, BorderLayout.NORTH);

        if (nombreProducto != null && !nombreProducto.isBlank()) {
            try {
                productoActual = controlador.searchProducto(nombreProducto);
                esHardware = productoActual instanceof Hardware;
                construirFormulario();
            } catch (EProductoNoEncontrado ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
                new GestionarProductos().setVisible(true);
                dispose();
                return;
            }
        } else {
            construirSelectorProducto(contentPane);
        }

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        JButton btnActualizar = UIComponents.crearBotonPrincipal("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        panelBotones.add(btnActualizar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void construirSelectorProducto(JPanel contentPane) {
        JPanel panel = UIComponents.crearPanelTarjeta();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JLabel lbl = new JLabel("Seleccione producto:");
        lbl.setFont(UITheme.FONT_ETIQUETA);
        panel.add(lbl);
        String[] nombres = controlador.getNombresProductos();
        JComboBox<String> cmbProductos = new JComboBox<>(nombres.length > 0 ? nombres : new String[]{"(No hay productos)"});
        cmbProductos.setPreferredSize(new Dimension(280, 30));
        panel.add(cmbProductos);
        JButton btnCargar = UIComponents.crearBotonPrincipal("Cargar");
        btnCargar.addActionListener(e -> {
            String sel = (String) cmbProductos.getSelectedItem();
            if (sel == null || sel.isBlank() || "(No hay productos)".equals(sel)) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                productoActual = controlador.searchProducto(sel);
                esHardware = productoActual instanceof Hardware;
                getContentPane().removeAll();
                getContentPane().setLayout(new BorderLayout(UITheme.ESPACIADO, UITheme.ESPACIADO));
                getContentPane().add(panelSuperiorDesde(contentPane), BorderLayout.NORTH);
                construirFormulario();
                getContentPane().add(panelBotonesDesde(contentPane), BorderLayout.SOUTH);
                revalidate();
                repaint();
            } catch (EProductoNoEncontrado ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnCargar);
        contentPane.add(panel, BorderLayout.CENTER);
    }

    private JPanel panelSuperiorDesde(JPanel contentPane) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JButton btnRegresar = UIComponents.crearBotonRegresar();
        btnRegresar.addActionListener(ev -> {
            new GestionarProductos().setVisible(true);
            dispose();
        });
        p.add(btnRegresar);
        return p;
    }

    private JPanel panelBotonesDesde(JPanel contentPane) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);
        JButton btnActualizar = UIComponents.crearBotonPrincipal("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        p.add(btnActualizar);
        return p;
    }

    private void construirFormulario() {
        panelForm = UIComponents.crearPanelTarjeta();
        panelForm.setLayout(new GridLayout(0, 1, 0, UITheme.ESPACIADO));

        txtNombre = UIComponents.crearCampoTexto(320);
        txtNombre.setText(productoActual.getNombre());
        txtNombre.setEditable(false);
        panelForm.add(crearFilaDosLineas("Nombre del producto:", txtNombre));

        txtDescripcion = UIComponents.crearCampoTexto(320);
        txtDescripcion.setText(productoActual.getDescripcion() != null ? productoActual.getDescripcion() : "");
        panelForm.add(crearFila("Descripción: ", txtDescripcion));

        txtCategoria = UIComponents.crearCampoTexto(250);
        txtCategoria.setText(productoActual.getCategoria() != null ? productoActual.getCategoria() : "");
        panelForm.add(crearFila("Categoría: ", txtCategoria));

        txtTiempoGarantia = UIComponents.crearCampoTexto(80);
        txtTiempoGarantia.setText(String.valueOf(productoActual.getTiempoGarantia()));
        panelForm.add(crearFila("Tiempo garantía (meses): ", txtTiempoGarantia));

        txtPrecioBase = UIComponents.crearCampoTexto(120);
        txtPrecioBase.setText(String.valueOf(productoActual.getPrecioBase()));
        panelForm.add(crearFila("Precio base: ", txtPrecioBase));

        txtStock = UIComponents.crearCampoTexto(80);
        txtStock.setText(String.valueOf(productoActual.getStock()));
        if (controlador.getCurrentUser() != null && controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            txtStock.setEnabled(false);
            txtStock.setToolTipText("No tiene permisos para modificar el stock.");
        }
        panelForm.add(crearFila("Stock: ", txtStock));

        JPanel rowDescuento = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowDescuento.setOpaque(false);
        chkDescuentoActivo = new JCheckBox("Activar descuento", productoActual.isDescuentoActivo());
        rowDescuento.add(chkDescuentoActivo);
        panelForm.add(rowDescuento);

        if (esHardware) {
            Hardware h = (Hardware) productoActual;
            txtConsumo = UIComponents.crearCampoTexto(100);
            txtConsumo.setText(String.valueOf(h.getConsumo()));
            panelForm.add(crearFilaDosLineas("Consumo (W):", txtConsumo));
            txtFabricante = UIComponents.crearCampoTexto(280);
            txtFabricante.setText(h.getFabricante() != null ? h.getFabricante() : "");
            panelForm.add(crearFilaDosLineas("Fabricante:", txtFabricante));
        } else {
            Videojuego v = (Videojuego) productoActual;
            txtDesarrollador = UIComponents.crearCampoTexto(220);
            txtDesarrollador.setText(v.getDesarrollador() != null ? v.getDesarrollador() : "");
            panelForm.add(crearFila("Desarrollador: ", txtDesarrollador));
            txtGenero = UIComponents.crearCampoTexto(180);
            txtGenero.setText(v.getGenero() != null ? v.getGenero() : "");
            panelForm.add(crearFila("Género: ", txtGenero));
            chkMultijugador = new JCheckBox();
            chkMultijugador.setSelected(v.getMultijugador());
            JPanel rowMult = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowMult.setOpaque(false);
            rowMult.add(new JLabel("Multijugador:"));
            rowMult.add(chkMultijugador);
            panelForm.add(rowMult);
            txtFechaLanzamiento = UIComponents.crearCampoTexto(120);
            Date f = v.getFechaLanzamiento();
            txtFechaLanzamiento.setText(f != null ? new SimpleDateFormat("dd/MM/yyyy").format(f) : "");
            panelForm.add(crearFila("Fecha lanzamiento (dd/MM/yyyy): ", txtFechaLanzamiento));
            txtPlataforma = UIComponents.crearCampoTexto(150);
            txtPlataforma.setText(v.getPlataforma() != null ? v.getPlataforma() : "");
            panelForm.add(crearFila("Plataforma: ", txtPlataforma));
            txtTamano = UIComponents.crearCampoTexto(80);
            txtTamano.setText(String.valueOf(v.getTamano()));
            panelForm.add(crearFila("Tamaño (GB): ", txtTamano));
        }

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        scrollForm.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollForm.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollForm, BorderLayout.CENTER);
    }

    private JPanel crearFila(String label, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_ETIQUETA);
        row.add(lbl);
        row.add(field);
        return row;
    }

    private JPanel crearFilaDosLineas(String label, JTextField field) {
        JPanel row = new JPanel(new GridLayout(2, 1, 0, 2));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_ETIQUETA);
        row.add(lbl);
        row.add(field);
        return row;
    }

    //TODO: verificar este método y entenderlo
    private void actualizar() {
        if (productoActual == null) {
            JOptionPane.showMessageDialog(this, "Debe cargar un producto primero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String desc = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";
        String cat = txtCategoria.getText() != null ? txtCategoria.getText().trim() : "";
        String tiempoStr = txtTiempoGarantia.getText();
        String precioStr = txtPrecioBase.getText();
        String stockStr = txtStock.getText();

        if (cat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La categoría es obligatoria.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Validación para Tiempo de Garantía
        if (tiempoStr == null || tiempoStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El campo 'Tiempo de Garantía' es obligatorio.", "Campo faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación para Precio Base
        if (precioStr == null || precioStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El campo 'Precio Base' es obligatorio.", "Campo faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación para Stock
        if (stockStr == null || stockStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El campo 'Stock' es obligatorio.", "Campo faltante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tiempoGarantia, stock;
        double precioBase;
        try {
            tiempoGarantia = Integer.parseInt(tiempoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El tiempo de garantía debe ser un número entero válido (ej: 12).", "Tiempo de garantía inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            precioBase = Double.parseDouble(precioStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio base debe ser un número válido (ej: 100000 o 99.99).", "Precio base inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            stock = Integer.parseInt(stockStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero válido (ej: 10).", "Stock inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controlador.getCurrentUser() != null && controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            stock = productoActual.getStock();
        }

        boolean exito;
        if (esHardware) {
            exito = actualizarHardwareDesdeForm(nombre, desc, cat, tiempoGarantia, precioBase, stock);
        } else {
            exito = actualizarVideojuegoDesdeForm(nombre, desc, cat, tiempoGarantia, precioBase, stock);
        }
        if (!exito) {
            return;
        }

        try {
            Producto actualizado = controlador.searchProducto(nombre);
            String msg = actualizado.isDescuentoActivo()
                    ? String.format("Producto actualizado correctamente.%nSe asignó un descuento del %d%% según stock y características.", (int) Math.round(actualizado.getDescuento() * 100))
                    : "Producto actualizado correctamente. Descuento desactivado.";
            JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarProductos().setVisible(true);
            dispose();
        } catch (EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida y ejecuta la actualización de hardware. Muestra errores al usuario.
     * @return true si la actualización fue exitosa, false si hubo error de validación o del controlador
     */
    private boolean actualizarHardwareDesdeForm(String nombre, String desc, String cat, int tiempoGarantia, double precioBase, int stock) {
        String fabricante = txtFabricante.getText() != null ? txtFabricante.getText().trim() : "";
        if (fabricante.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El fabricante es obligatorio.", "Campo requerido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String consumoStr = txtConsumo != null ? txtConsumo.getText() : null;
        if (consumoStr == null || consumoStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El consumo es obligatorio.", "Campo requerido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        float consumo;
        try {
            consumo = Float.parseFloat(consumoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El consumo debe ser un número válido (ej: 150 o 85.5).", "Consumo inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            controlador.actualizarHardware(nombre, desc, cat, tiempoGarantia, precioBase, stock, consumo, fabricante, chkDescuentoActivo.isSelected());
            return true;
        } catch (EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (EParametroNulo | ECantidadNegativa | EValorNegativo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Valida y ejecuta la actualización de videojuego. Muestra errores al usuario.
     * @return true si la actualización fue exitosa, false si hubo error de validación o del controlador
     */
    private boolean actualizarVideojuegoDesdeForm(String nombre, String desc, String cat, int tiempoGarantia, double precioBase, int stock) {
        String desarrollador = txtDesarrollador.getText() != null ? txtDesarrollador.getText().trim() : "";
        if (desarrollador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El desarrollador es obligatorio.", "Campo requerido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String genero = txtGenero.getText() != null ? txtGenero.getText().trim() : "";
        if (genero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El género es obligatorio.", "Campo requerido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String plataforma = txtPlataforma.getText() != null ? txtPlataforma.getText().trim() : "";
        if (plataforma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La plataforma es obligatoria.", "Campo requerido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String tamanoStr = txtTamano != null ? txtTamano.getText() : null;
        if (tamanoStr == null || tamanoStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "El tamaño es obligatorio.", "Campo requerido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        double tamano;
        try {
            tamano = Double.parseDouble(tamanoStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El tamaño debe ser un número válido (ej: 50 o 12.5).", "Tamaño inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Date fechaLanzamiento = null;
        String fechaStr = txtFechaLanzamiento.getText();
        if (fechaStr != null && !fechaStr.isBlank()) {
            try {
                fechaLanzamiento = new SimpleDateFormat("dd/MM/yyyy").parse(fechaStr.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "La fecha de lanzamiento debe estar en formato dd/MM/yyyy.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        try {
            controlador.actualizarVideojuego(nombre, desc, cat, tiempoGarantia, precioBase, stock,
                    desarrollador, genero, chkMultijugador.isSelected(), fechaLanzamiento, plataforma, tamano, chkDescuentoActivo.isSelected());
            return true;
        } catch (EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (EParametroNulo | ECantidadNegativa | EValorNegativo ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
