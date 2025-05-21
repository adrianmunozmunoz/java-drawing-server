package main;

import figuras.*;
import usuarios.*;
import dibujos.Dibujo;

import java.util.Arrays;

public class MainEjercicio2 {
    public static void main(String[] args) {
        System.out.println("\n◆ Creando dibujo...");

        // Crear un dueño de dibujo (Administrador del mismo)
        DuenoDeDibujo dueno = new DuenoDeDibujo("Ana", "admin456");
        Dibujo dibujo = new Dibujo(dueno);

        // Crear un usuario normal y otro con permisos de edición
        Usuario usuarioNormal = new Usuario("Juan", "pass123");
        Usuario usuarioEditor = new Usuario("Pedro", "edit123");

        // Otorgar permisos
        dibujo.otorgarPermisoVer(usuarioNormal);
        dibujo.otorgarPermisoEditar(usuarioEditor);

        System.out.println("\n◆ Permisos asignados correctamente.");

        // Agregar figuras al dibujo con el usuario que tiene permiso de edición
        dibujo.agregarFigura(new Circulo(5, 5, 10), usuarioEditor);
        dibujo.agregarFigura(new Rectangulo(2, 3, 8, 4), usuarioEditor);
        dibujo.agregarFigura(new Elipse(7, 7, 5, 4), usuarioEditor);

        System.out.println("\n◆ Figuras agregadas al dibujo.");
        System.out.println(dibujo.obtenerDescripcion(usuarioEditor));

        // Mover una figura (ID 1, por ejemplo)
        dibujo.moverFigura(1, 10, 10, usuarioEditor);
        System.out.println("\n◆ Figura 1 movida.");
        System.out.println(dibujo.obtenerDescripcion(usuarioEditor));

        // Agrupar figuras (IDs 1 y 2)
        dibujo.agruparFiguras(Arrays.asList(1, 2, 3), usuarioEditor);
        System.out.println("\n◆ Figuras agrupadas.");
        System.out.println(dibujo.obtenerDescripcion(usuarioEditor));

        // Desagrupar la figura con ID 3
        dibujo.desagruparFiguras(4, usuarioEditor);
        System.out.println("\n◆ Figura desagrupada.");
        System.out.println(dibujo.obtenerDescripcion(usuarioEditor));

        // Eliminar una figura
        dibujo.eliminarFigura(6, usuarioEditor);
        System.out.println("\n◆ Figura eliminada.");
        System.out.println(dibujo.obtenerDescripcion(usuarioEditor));

        System.out.println("\n✅ Ejercicio 2 completado correctamente.");
    }
}

