package figuras;

public class Circulo extends Elipse {
    public Circulo(int x, int y, int radio) {
        super(x, y, radio, radio);
        if (radio <= 0) {
            throw new IllegalArgumentException("El radio debe ser positivo");
        }
    }

    public static Figura parse(String... args) {
        if (args.length != 3) {
            throw new RuntimeException("Error al parsear Circulo: se esperaban 3 argumentos");
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int radio = Integer.parseInt(args[2]);
            return new Circulo(x, y, radio);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error al parsear Circulo: argumentos numéricos incorrectos", e);
        }
    }

    @Override
    public String toString() {
        return "Circulo " + coordenadas.getX() + " " + coordenadas.getY() + " " + rmax;
    }
}