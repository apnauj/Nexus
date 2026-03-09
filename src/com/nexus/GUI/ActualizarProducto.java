package com.nexus.GUI;

import com.nexus.controller.StoreController;
import com.nexus.exceptions.ECantidadNegativa;
import com.nexus.exceptions.EParametroNulo;
import com.nexus.exceptions.EProductoNoEncontrado;
import com.nexus.exceptions.EValorNegativo;
import com.nexus.model.entities.Hardware;
import com.nexus.model.entities.Producto;
import com.nexus.model.entities.Videojuego;
import com.nexus.model.enums.Rol;

import javax.swing.Box;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private JTextField txtDesarrolladores;
    private JTextField txtGeneros;
    private JCheckBox chkMultijugador;
    private JTextField txtFechaLanzamiento;
    private JTextField txtPlataforma;
    private JTextField txtTamano;

    private JPanel panelForm;
    private JPanel panelEspecifico;

    public ActualizarProducto() {
        this(null);
    }

    public ActualizarProducto(String nombreProducto) {
        controlador = StoreController.getInstance();
        setTitle("Nexus Store - Actualizar Producto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        com.nexus.NexusApplication.addGuardarAlCerrar(this, controlador);
        setBounds(100, 100, 540, 620);
        setResizable(true);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(5, 5));
        setContentPane(contentPane);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegresar = new JButton("Regresar");
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
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        panelBotones.add(btnActualizar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);
    }

    private void construirSelectorProducto(JPanel contentPane) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Seleccione producto: "));
        String[] nombres = controlador.getNombresProductos();
        JComboBox<String> cmbProductos = new JComboBox<>(nombres.length > 0 ? nombres : new String[]{"(No hay productos)"});
        cmbProductos.setPreferredSize(new Dimension(250, 25));
        panel.add(cmbProductos);
        JButton btnCargar = new JButton("Cargar");
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
                getContentPane().setLayout(new BorderLayout(5, 5));
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
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(ev -> {
            new GestionarProductos().setVisible(true);
            dispose();
        });
        p.add(btnRegresar);
        return p;
    }

    private JPanel panelBotonesDesde(JPanel contentPane) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        p.add(btnActualizar);
        return p;
    }

    private void construirFormulario() {
        panelForm = new JPanel(new GridLayout(0, 1, 0, 6));
        panelForm.setPreferredSize(new Dimension(500, 400));

        txtNombre = crearTextField(35);
        txtNombre.setText(productoActual.getNombre());
        txtNombre.setEditable(false);
        panelForm.add(crearFilaDosLineas("Nombre del producto:", txtNombre));

        txtDescripcion = crearTextField(35);
        txtDescripcion.setText(productoActual.getDescripcion() != null ? productoActual.getDescripcion() : "");
        panelForm.add(crearFila("Descripción: ", txtDescripcion));

        txtCategoria = crearTextField(28);
        txtCategoria.setText(productoActual.getCategoria() != null ? productoActual.getCategoria() : "");
        panelForm.add(crearFila("Categoría: ", txtCategoria));

        txtTiempoGarantia = crearTextField(8);
        txtTiempoGarantia.setText(String.valueOf(productoActual.getTiempoGarantia()));
        panelForm.add(crearFila("Tiempo garantía (meses): ", txtTiempoGarantia));

        txtPrecioBase = crearTextField(12);
        txtPrecioBase.setText(String.valueOf(productoActual.getPrecioBase()));
        panelForm.add(crearFila("Precio base: ", txtPrecioBase));

        txtStock = crearTextField(8);
        txtStock.setText(String.valueOf(productoActual.getStock()));
        if (controlador.getCurrentUser() != null && controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            txtStock.setEnabled(false);
            txtStock.setToolTipText("No tiene permisos para modificar el stock.");
        }
        panelForm.add(crearFila("Stock: ", txtStock));

        panelEspecifico = new JPanel(new GridLayout(0, 1, 0, 6));
        if (esHardware) {
            Hardware h = (Hardware) productoActual;
            txtConsumo = crearTextField(8);
            txtConsumo.setText(String.valueOf(h.getConsumo()));
            panelEspecifico.add(crearFila("Consumo (W): ", txtConsumo));
            txtFabricante = crearTextField(20);
            txtFabricante.setText(h.getFabricante() != null ? h.getFabricante() : "");
            panelEspecifico.add(crearFila("Fabricante: ", txtFabricante));
        } else {
            Videojuego v = (Videojuego) productoActual;
            txtDesarrolladores = crearTextField(38);
            String[] devs = v.getDesarrolladores();
            txtDesarrolladores.setText(devs != null ? String.join(", ", devs) : "");
            panelEspecifico.add(crearFilaDosLineas("Desarrolladores (separados por coma):", txtDesarrolladores));
            txtGeneros = crearTextField(38);
            String[] gens = v.getGeneros();
            txtGeneros.setText(gens != null ? String.join(", ", gens) : "");
            panelEspecifico.add(crearFilaDosLineas("Géneros (separados por coma):", txtGeneros));
            chkMultijugador = new JCheckBox();
            chkMultijugador.setSelected(v.getMultijugador());
            JPanel rowMult = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowMult.add(new JLabel("Multijugador: "));
            rowMult.add(chkMultijugador);
            panelEspecifico.add(rowMult);
            txtFechaLanzamiento = crearTextField(12);
            Date f = v.getFechaLanzamiento();
            txtFechaLanzamiento.setText(f != null ? new SimpleDateFormat("dd/MM/yyyy").format(f) : "");
            panelEspecifico.add(crearFila("Fecha lanzamiento (dd/MM/yyyy): ", txtFechaLanzamiento));
            txtPlataforma = crearTextField(15);
            txtPlataforma.setText(v.getPlataforma() != null ? v.getPlataforma() : "");
            panelEspecifico.add(crearFila("Plataforma: ", txtPlataforma));
            txtTamano = crearTextField(8);
            txtTamano.setText(String.valueOf(v.getTamano()));
            panelEspecifico.add(crearFila("Tamaño (GB): ", txtTamano));
        }
        panelForm.add(panelEspecifico);
        panelForm.add(Box.createVerticalStrut(12));

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(null);
        scrollForm.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollForm.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollForm, BorderLayout.CENTER);
    }

    private JPanel crearFila(String label, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }

    private JPanel crearFilaDosLineas(String label, JTextField field) {
        JPanel row = new JPanel(new GridLayout(2, 1, 0, 2));
        row.add(new JLabel(label));
        row.add(field);
        return row;
    }

    private JTextField crearTextField(int cols) {
        JTextField t = new JTextField(cols);
        t.setForeground(Color.BLACK);
        t.setBackground(Color.WHITE);
        t.setCaretColor(Color.BLACK);
        return t;
    }

    private void actualizar() {
        if (productoActual == null) {
            JOptionPane.showMessageDialog(this, "Debe cargar un producto primero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText();
        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre del producto es obligatorio.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String desc = txtDescripcion.getText();
        String cat = txtCategoria.getText();
        String tiempoStr = txtTiempoGarantia.getText();
        String precioStr = txtPrecioBase.getText();
        String stockStr = txtStock.getText();

        if (cat == null || cat.isBlank()) {
            JOptionPane.showMessageDialog(this, "La categoría es obligatoria.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tiempoGarantia;
        double precioBase;
        int stock;
        try {
            tiempoGarantia = Integer.parseInt(tiempoStr.trim());
            precioBase = Double.parseDouble(precioStr.trim());
            stock = Integer.parseInt(stockStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique que tiempo, precio y stock sean números válidos.", "Formato inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controlador.getCurrentUser() != null && controlador.getCurrentUser().getRol() == Rol.EMPLEADO_VENTAS) {
            stock = productoActual.getStock();
        }

        try {
            if (esHardware) {
                actualizarHardwareDesdeForm(nombre, desc, cat, tiempoGarantia, precioBase, stock);
            } else {
                actualizarVideojuegoDesdeForm(nombre, desc, cat, tiempoGarantia, precioBase, stock);
            }
            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new GestionarProductos().setVisible(true);
            dispose();
        } catch (EProductoNoEncontrado ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (EParametroNulo | EValorNegativo | ECantidadNegativa ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarHardwareDesdeForm(String nombre, String desc, String cat, int tiempoGarantia, double precioBase, int stock) throws EProductoNoEncontrado, EParametroNulo, ECantidadNegativa, EValorNegativo {
        String fabricante = txtFabricante.getText();
        if (fabricante == null || fabricante.isBlank()) {
            throw new EParametroNulo("fabricante");
        }
        float consumo;
        try {
            consumo = Float.parseFloat(txtConsumo.getText().trim());
        } catch (NumberFormatException e) {
            throw new ECantidadNegativa("El consumo debe ser un número válido.");
        }
        controlador.actualizarHardware(nombre, desc, cat, tiempoGarantia, precioBase, stock, consumo, fabricante);
    }

    private void actualizarVideojuegoDesdeForm(String nombre, String desc, String cat, int tiempoGarantia, double precioBase, int stock) throws EProductoNoEncontrado, EParametroNulo, ECantidadNegativa, EValorNegativo {
        String devStr = txtDesarrolladores.getText();
        String genStr = txtGeneros.getText();
        if (devStr == null) devStr = "";
        if (genStr == null) genStr = "";
        String[] desarrolladores = Arrays.stream(devStr.trim().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        String[] generos = Arrays.stream(genStr.trim().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        if (desarrolladores.length == 0) {
            throw new EParametroNulo("desarrolladores");
        }
        if(generos.length == 0) {
        	throw new EParametroNulo("generos");
        }
        String plataforma = txtPlataforma.getText();
        if (plataforma == null || plataforma.isBlank()) {
            throw new EParametroNulo("plataforma");
        }
        double tamano;
        try {
            tamano = Double.parseDouble(txtTamano.getText().trim());
        } catch (NumberFormatException e) {
            throw new ECantidadNegativa("El tamaño debe ser un número válido.");
        }
        Date fechaLanzamiento = null;
        String fechaStr = txtFechaLanzamiento.getText();
        if (fechaStr != null && !fechaStr.isBlank()) {
            try {
                fechaLanzamiento = new SimpleDateFormat("dd/MM/yyyy").parse(fechaStr.trim());
            } catch (Exception e) {
                throw new EParametroNulo("fechaLanzamiento");
            }
        }
        controlador.actualizarVideojuego(nombre, desc, cat, tiempoGarantia, precioBase, stock,
                desarrolladores, generos, chkMultijugador.isSelected(), fechaLanzamiento, plataforma, tamano);
    }
}
