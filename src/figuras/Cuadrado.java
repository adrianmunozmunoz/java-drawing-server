package figuras;

public class Cuadrado extends Rectangulo {
    public Cuadrado(int x, int y, int lado) {
        super(x, y, lado, lado);
        if (lado <= 0) {
            throw new IllegalArgumentException("El lado debe ser positivo");
        }
    }

    public static Figura parse(String... args) {
        if (args.length != 3) {
            throw new RuntimeException("Error al parsear Cuadrado: se esperaban 3 argumentos");
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int lado = Integer.parseInt(args[2]);
            return new Cuadrado(x, y, lado);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error al parsear Cuadrado: argumentos numéricos incorrectos", e);
        }
    }

    @Override
    public String toString() {
        return "Cuadrado " + coordenadas.getX() + " " + coordenadas.getY() + " " + anchura;
    }
}

