package figuras;

import coordenadas.Coordenadas;

public abstract class Figura {
    protected Coordenadas coordenadas;

    public Figura(int x, int y) {
        this.coordenadas = new Coordenadas(x, y);
    }

    // Permite mover la figura actualizando sus coordenadas
    public void mover(int dx, int dy) {
        this.coordenadas = new Coordenadas(coordenadas.getX() + dx, coordenadas.getY() + dy);
    }

    // Determina si el punto dado se encuentra dentro de la figura
    public abstract boolean contienePunto(Coordenadas coordenadas);

    @Override
    public abstract String toString();
}