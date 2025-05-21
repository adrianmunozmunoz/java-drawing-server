package figuras;

public class Punto extends Figura {

    public Punto(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean contienePunto(coordenadas.Coordenadas coord) {
        return this.coordenadas.equals(coord);
    }

    public static Figura parse(String... args) {
        if (args.length != 2) {
            throw new RuntimeException("Error al parsear Punto: se esperaban 2 argumentos");
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            return new Punto(x, y);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error al parsear Punto: argumentos numéricos incorrectos", e);
        }
    }

    @Override
    public String toString() {
        return "Punto " + coordenadas.getX() + " " + coordenadas.getY();
    }
}