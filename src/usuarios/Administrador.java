package usuarios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Administrador extends Usuario {
    private final List<Usuario> usuarios;

    public Administrador(String nombre, String contrasena) {
        super(nombre, contrasena);
        this.usuarios = new ArrayList<>();
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public boolean gestionarUsuarios(Usuario usuario) {
        if (usuarios.contains(usuario)) {
            return false;
        } else {
            usuarios.add(usuario);
            return true;
        }
    }

    public boolean eliminarUsuario(Usuario usuario) {
        return usuarios.remove(usuario);
    }
}

