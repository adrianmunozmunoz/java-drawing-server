package main;

import figuras.*;
import usuarios.*;

import java.util.ArrayList;
import java.util.List;

public class MainEjercicio1 {
    public static void main(String[] args) {
        System.out.println("🔹 Creando Figuras...");

        // Crear diferentes figuras
        Figura circulo = new Circulo(5, 5, 10);
        Figura rectangulo = new Rectangulo(2, 3, 8, 4);
        Figura cuadrado = new Cuadrado(1, 1, 5);
        Figura elipse = new Elipse(4, 4, 6, 3);
        Figura punto = new Punto(7, 7);

        // Crear un grupo de figuras
        List<Figura> listaFiguras = new ArrayList<>();
        listaFiguras.add(new Circulo(10, 10, 15));
        listaFiguras.add(new Rectangulo(3, 4, 5, 6));
        listaFiguras.add(new Punto(12, 12));
        Figura grupo = new GrupoDeFiguras(listaFiguras);

        // Mostrar figuras creadas
        System.out.println(circulo);
        System.out.println(rectangulo);
        System.out.println(cuadrado);
        System.out.println(elipse);
        System.out.println(punto);
        System.out.println(grupo);

        System.out.println("\n🔹 Creando Usuarios...");

        // Crear usuarios
        Usuario usuarioNormal = new Usuario("Juan", "password123");
        Usuario duenoDibujo = new DuenoDeDibujo("Ana", "password456");
        Usuario admin = new Administrador("Carlos", "adminPass");

        // Mostrar usuarios creados
        System.out.println(usuarioNormal);
        System.out.println(duenoDibujo);
        System.out.println(admin);

        System.out.println("\n✅ Ejercicio 1 completado correctamente.");
    }
}
