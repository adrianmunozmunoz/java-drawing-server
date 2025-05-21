package dibujos;

import figuras.*;
import coordenadas.Coordenadas;
import usuarios.Usuario;
import parsing.FiguraParser;

import java.io.*;
import java.util.*;

public class Dibujo {
    protected final Usuario administrador;
    protected final List<Figura> figuras;
    private final Set<Usuario> permisosVer;
    private final Set<Usuario> permisosEditar;
    private int contadorId = 0;
    private final Map<Integer, Figura> figurasPorId;

    public Dibujo(Usuario administrador) {
        this.administrador = administrador;
        this.figuras = new ArrayList<>();
        this.permisosVer = new HashSet<>();
        this.permisosEditar = new HashSet<>();
        this.figurasPorId = new HashMap<>();

        permisosVer.add(administrador);
        permisosEditar.add(administrador);
    }

    public void saveTo(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<Integer, Figura> entry : figurasPorId.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue().toString());
                writer.newLine();
            }
            // Se podría usar un logger en vez de System.out.println
            System.out.println("Dibujo guardado en " + filename);
        } catch (IOException e) {
            System.err.println("Error al guardar el dibujo: " + e.getMessage());
        }
    }

    public void loadFrom(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            figuras.clear();
            figurasPorId.clear();
            contadorId = 0;

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\s+", 2);
                int id = Integer.parseInt(partes[0]);
                Figura figura = FiguraParser.parse(partes[1]);
                figurasPorId.put(id, figura);
            }
            System.out.println("Dibujo cargado desde " + filename);
        } catch (IOException e) {
            System.err.println("Error al cargar el dibujo: " + e.getMessage());
        }
    }

    public void otorgarPermisoVer(Usuario usuario) {
        permisosVer.add(usuario);
    }

    public void otorgarPermisoEditar(Usuario usuario) {
        permisosVer.add(usuario);
        permisosEditar.add(usuario);
    }

    public boolean puedeVer(Usuario usuario) {
        return permisosVer.contains(usuario);
    }

    public boolean noPuedeEditar(Usuario usuario) {
        return !permisosEditar.contains(usuario);
    }

    private void registrarFigura(Figura figura) {
        contadorId++;
        figuras.add(figura);
        figurasPorId.put(contadorId, figura);
    }

    public synchronized boolean agregarFigura(Figura figura, Usuario usuario) {
        if (noPuedeEditar(usuario)) {
            return false;
        }
        registrarFigura(figura);
        return true;
    }

    public synchronized boolean eliminarFigura(int idFigura, Usuario usuario) {
        if (noPuedeEditar(usuario)) {
            return false;
        }
        Figura figura = figurasPorId.remove(idFigura);
        if (figura == null) {
            return false;
        }
        figuras.remove(figura);
        return true;
    }

    public synchronized boolean moverFigura(int idFigura, int dx, int dy, Usuario usuario) {
        if (noPuedeEditar(usuario)) {
            return false;
        }
        Figura figura = figurasPorId.get(idFigura);
        if (figura == null) {
            return false;
        }
        figura.mover(dx, dy);
        return true;
    }

    public String obtenerDescripcion(Usuario usuario) {
        if (!puedeVer(usuario)) {
            throw new IllegalArgumentException("No tienes permisos para ver este dibujo.");
        }

        StringBuilder descripcion = new StringBuilder("Dibujo:");
        for (Map.Entry<Integer, Figura> entry : figurasPorId.entrySet()) {
            descripcion.append("\nID ").append(entry.getKey())
                    .append(": ").append(entry.getValue());
        }
        return descripcion.toString();
    }

    public void agruparFiguras(List<Integer> ids, Usuario usuario) {
        if (noPuedeEditar(usuario)) {
            throw new IllegalArgumentException("No tienes permisos para editar este dibujo.");
        }

        List<Figura> figurasAgrupadas = new ArrayList<>();
        for (Integer id : ids) {
            Figura figura = figurasPorId.remove(id);
            if (figura != null) {
                figurasAgrupadas.add(figura);
                figuras.remove(figura);
            }
        }

        if (figurasAgrupadas.isEmpty()) {
            throw new IllegalArgumentException("No hay figuras válidas para agrupar.");
        }

        GrupoDeFiguras grupo = new GrupoDeFiguras();
        for (Figura figura : figurasAgrupadas) {
            grupo.agregarFigura(figura);
        }

        registrarFigura(grupo);
    }

    // Desagrupa un grupo de figuras identificado por su ID, volviendo a añadir cada subfigura individualmente
    public void desagruparFiguras(int id, Usuario usuario) {
        if (noPuedeEditar(usuario)) {
            throw new IllegalArgumentException("No tienes permisos para editar este dibujo.");
        }
        Figura figura = figurasPorId.remove(id);
        if (figura == null) {
            throw new IllegalArgumentException("No existe ninguna figura con id " + id + ".");
        }
        if (!(figura instanceof GrupoDeFiguras grupo)) {
            throw new IllegalArgumentException("La figura con id " + id + " no es un grupo.");
        }

        for (Figura subFigura : grupo.getFiguras()) {
            registrarFigura(subFigura);
        }
    }

    public boolean contieneFigura(Figura figura) {
        return figuras.contains(figura);
    }

    public synchronized List<Integer> localizarFiguras(Coordenadas coordenadas) {
        List<Integer> figurasEncontradas = new ArrayList<>();
        for (Map.Entry<Integer, Figura> entry : figurasPorId.entrySet()) {
            if (entry.getValue().contienePunto(coordenadas)) {
                figurasEncontradas.add(entry.getKey());
            }
        }
        return figurasEncontradas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Dibujo:\n");
        for (Map.Entry<Integer, Figura> entry : figurasPorId.entrySet()) {
            sb.append("ID ").append(entry.getKey())
                    .append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}