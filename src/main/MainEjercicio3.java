package main;

import dibujos.Dibujo;
import figuras.*;
import usuarios.DuenoDeDibujo;

import java.io.File;

public class MainEjercicio3 {
    public static void main(String[] args) {
        System.out.println("\n Creando dibujo...");

        // Crear un dueño de dibujo (Administrador del mismo)
        DuenoDeDibujo dueno = new DuenoDeDibujo("Ana", "admin456");
        Dibujo dibujo = new Dibujo(dueno);

        // Agregar figuras al dibujo
        dibujo.agregarFigura(new Circulo(5, 5, 10), dueno);
        dibujo.agregarFigura(new Rectangulo(2, 3, 8, 4), dueno);
        dibujo.agregarFigura(new Punto(7, 7), dueno);
        dibujo.agregarFigura(new Elipse(10, 10, 6, 3), dueno);

        System.out.println("\n Figuras agregadas al dibujo:");
        System.out.println(dibujo.obtenerDescripcion(dueno));

        // Guardar en archivo
        String archivo = "dibujo.txt";
        dibujo.saveTo(archivo);
        System.out.println("\n Dibujo guardado en " + archivo);

        // Crear un nuevo dibujo vacío y cargar desde archivo
        Dibujo dibujoCargado = new Dibujo(dueno);
        dibujoCargado.loadFrom(archivo);

        System.out.println("\n Dibujo cargado desde archivo:");
        System.out.println(dibujoCargado.obtenerDescripcion(dueno));

        // Comprobación final
        if (new File(archivo).exists()) {
            System.out.println("\n Ejercicio 3 completado correctamente.");
        } else {
            System.err.println("\n Error: No se encontró el archivo guardado.");
        }
    }
}

