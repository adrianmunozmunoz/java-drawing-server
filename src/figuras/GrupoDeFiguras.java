package figuras;

import coordenadas.Coordenadas;
import java.util.ArrayList;
import java.util.List;

public class GrupoDeFiguras extends Figura {
    private final List<Figura> figuras;

    public GrupoDeFiguras() {
        super(0, 0); // Las coordenadas del grupo no se utilizan directamente
        this.figuras = new ArrayList<>();
    }

    public GrupoDeFiguras(List<Figura> figuras) {
        super(0, 0);
        this.figuras = new ArrayList<>(figuras);
    }

    public List<Figura> getFiguras() {
        return figuras;
    }

    public void agregarFigura(Figura figura) {
        figuras.add(figura);
    }

    @Override
    public void mover(int dx, int dy) {
        for (Figura figura : figuras) {
            figura.mover(dx, dy);
        }
    }

    @Override
    public boolean contienePunto(Coordenadas coord) {
        for (Figura figura : figuras) {
            if (figura.contienePunto(coord)) {
                return true;
            }
        }
        return false;
    }

    public void eliminarFigura(Figura figura) {
        if (!figuras.remove(figura)) {
            System.out.println("La figura no se encuentra en el grupo de figuras");
        }
    }

    public static Figura parse(String... args) {
        List<Figura> figurasGrupo = new ArrayList<>();
        for (String arg : args) {
            try {
                figurasGrupo.add(new Punto(0, 0));
            } catch (Exception e) {
                throw new RuntimeException("Error al parsear Grupo: " + String.join(" ", args), e);
            }
        }
        return new GrupoDeFiguras(figurasGrupo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Grupo");
        for (Figura figura : figuras) {
            sb.append(" ").append(figura.hashCode());
        }
        return sb.toString();
    }
}