package figuras;

import coordenadas.Coordenadas;

public class Elipse extends Figura {
    protected int rmax, rmin;

    public Elipse(int x, int y, int rmax, int rmin) {
        super(x, y);
        if (rmax <= 0 || rmin <= 0) {
            throw new IllegalArgumentException("Los radios deben ser positivos");
        }
        this.rmax = rmax;
        this.rmin = rmin;
    }

    @Override
    public boolean contienePunto(Coordenadas coord) {
        int dx = coord.getX() - this.coordenadas.getX();
        int dy = coord.getY() - this.coordenadas.getY();
        double value = ((dx * dx) / (double) (rmax * rmax)) + ((dy * dy) / (double) (rmin * rmin));
        return value <= 1.0;
    }

    public static Figura parse(String... args) {
        if (args.length != 4) {
            throw new RuntimeException("Error al parsear Elipse: se esperaban 4 argumentos");
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int rmax = Integer.parseInt(args[2]);
            int rmin = Integer.parseInt(args[3]);
            return new Elipse(x, y, rmax, rmin);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error al parsear Elipse: argumentos numéricos incorrectos", e);
        }
    }

    @Override
    public String toString() {
        return "Elipse " + coordenadas.getX() + " " + coordenadas.getY() + " " + rmax + " " + rmin;
    }
}