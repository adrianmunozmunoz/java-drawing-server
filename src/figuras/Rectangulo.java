package figuras;

import coordenadas.Coordenadas;

public class Rectangulo extends Figura {
    protected int anchura, altura;

    public Rectangulo(int x, int y, int anchura, int altura) {
        super(x, y);
        if (anchura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("La anchura y la altura deben ser positivas");
        }
        this.anchura = anchura;
        this.altura = altura;
    }

    @Override
    public boolean contienePunto(Coordenadas coord) {
        int px = coord.getX();
        int py = coord.getY();
        int x1 = this.coordenadas.getX();
        int y1 = this.coordenadas.getY();
        return (px >= x1 && px <= x1 + anchura && py >= y1 && py <= y1 + altura);
    }

    public static Figura parse(String... args) {
        if (args.length != 4) {
            throw new RuntimeException("Error al parsear Rectangulo: se esperaban 4 argumentos");
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int anchura = Integer.parseInt(args[2]);
            int altura = Integer.parseInt(args[3]);
            return new Rectangulo(x, y, anchura, altura);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error al parsear Rectangulo: argumentos numéricos incorrectos", e);
        }
    }

    @Override
    public String toString() {
        return "Rectangulo " + coordenadas.getX() + " " + coordenadas.getY() + " " + anchura + " " + altura;
    }
}