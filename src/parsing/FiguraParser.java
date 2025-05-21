package parsing;

import figuras.*;
import java.util.*;

public class FiguraParser {

    @FunctionalInterface
    public interface Parser {
        Figura parse(String... args);
    }

    private static final Map<String, Parser> parsers = new HashMap<>();

    // Parsea una cadena y devuelve la figura correspondiente.
    public static Figura parse(String s) {
        try {
            String[] partes = s.split("\\s+"); // Separa la cadena por espacios
            String tipo = partes[0];
            Parser parser = parsers.get(tipo);
            if (parser == null) {
                throw new RuntimeException("Tipo de figura desconocido: " + tipo);
            }
            return parser.parse(Arrays.copyOfRange(partes, 1, partes.length));
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la figura: " + s, e);
        }
    }

    static {
        parsers.put("Punto", Punto::parse);
        parsers.put("Circulo", Circulo::parse);
        parsers.put("Elipse", Elipse::parse);
        parsers.put("Cuadrado", Cuadrado::parse);
        parsers.put("Rectangulo", Rectangulo::parse);
        parsers.put("Grupo", GrupoDeFiguras::parse);
    }
}