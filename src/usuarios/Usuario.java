package usuarios;

public class Usuario {
    protected final String nombre;
    protected final String contrasena;

    public Usuario(String nombre, String contrasena) {
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "Usr[" + nombre + "]";
    }

    public void dump(StringBuilder sb, String pref) {
        sb.append(pref).append("Nombre: ").append(nombre).append("\n");
        sb.append(pref).append("Contraseña: ").append(contrasena).append("\n");
    }

    public void dump() {
        var sb = new StringBuilder();
        dump(sb, "");
        System.err.println(sb);
    }
}