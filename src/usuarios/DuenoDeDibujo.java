package usuarios;

import java.util.ArrayList;
import java.util.List;

public class DuenoDeDibujo extends Usuario {
    private final List<Usuario> permisos;

    public DuenoDeDibujo(String nombre, String contrasena) {
        super(nombre, contrasena);
        this.permisos = new ArrayList<>();
    }

    public boolean gestionarPermisos(Usuario usuario) {
        if (permisos.contains(usuario)) {
            return false;
        } else {
            permisos.add(usuario);
            return true;
        }
    }

    public boolean revocarPermisos(Usuario usuario) {
        if (permisos.contains(usuario)) {
            permisos.remove(usuario);
            return true;
        } else {
            return false;
        }
    }

    public List<Usuario> getPermisos() {
        return new ArrayList<>(permisos);
    }
}
