# Nexus

Sistema de gestión para una tienda de videojuegos y hardware: catálogo, clientes, órdenes
de compra y control de inventario, con interfaz de escritorio en Swing.

Proyecto del curso de Programación Orientada a Objetos en la **Universidad EIA**. El punto
del ejercicio no era la tienda, sino modelar un dominio con herencia, encapsulamiento y
errores tipados en vez de validaciones dispersas.

**Stack** · Java · Swing · Serialización

---

## Cómo correrlo

```bash
git clone https://github.com/apnauj/Nexus.git
```

Abrir la carpeta en IntelliJ IDEA o Eclipse como proyecto Java y ejecutar
`com.nexus.NexusApplication`. No hay dependencias externas ni build tool: solo un JDK 17 o
superior.

---

## Modelo de dominio

`Producto` es una clase abstracta de la que heredan `Videojuego` y `Hardware`. Lo que las
distingue no es solo el conjunto de atributos, sino qué significa un producto válido en
cada caso — por eso la validación vive en el constructor de la clase base y nadie puede
construir un producto inconsistente:

```java
public Producto(String nombre, String descripcion, String categoria,
                int tiempoGarantia, double precioBase, int stock)
        throws EParametroNulo, EValorNegativo {
    if (nombre == null || nombre.isBlank())  throw new EParametroNulo("nombre del producto");
    if (precioBase < 0)                      throw new EValorNegativo(...);
    if (stock < 0)                           throw new EValorNegativo(...);
    ...
}
```

Una orden (`Orden`) agrupa líneas (`OrdenItem`) en vez de productos directamente, porque la
cantidad y el precio al momento de la compra pertenecen a la línea, no al producto: si
mañana cambia el precio del catálogo, las órdenes viejas no deben cambiar con él.

```
model/entities/   Producto (abstracta) → Videojuego, Hardware
                  Usuario → Cliente
                  Orden → OrdenItem
model/enums/      Estado, MetodoPago, Rol, TipoDocumento
service/          LoginService
controller/       StoreController — orquesta el dominio para la GUI
gui/              22 ventanas Swing
exceptions/       20 excepciones de dominio
```

---

## Decisiones

- **Veinte excepciones propias en vez de `IllegalArgumentException`.** `EStockInsuficiente`
  y `EProductoNoEncontrado` le dicen algo distinto a quien las atrapa; una excepción
  genérica obliga a leer el mensaje para saber qué pasó.
- **La GUI no lanza excepciones, las traduce.** Las ventanas atrapan las excepciones de
  dominio y las muestran como diálogos. El dominio no sabe que existe una interfaz gráfica.
- **Enums para estado, rol, método de pago y tipo de documento**, de modo que los valores
  válidos estén en un solo lugar.

## Limitaciones conocidas

- No hay pruebas automatizadas.
- La persistencia por serialización acopla los archivos guardados a la forma exacta de las
  clases: cambiar un atributo invalida los datos existentes.
- No hay build tool. Con Maven o Gradle el proyecto se abriría en cualquier entorno sin
  configuración manual.
