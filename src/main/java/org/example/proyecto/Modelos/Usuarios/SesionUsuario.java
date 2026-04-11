package org.example.proyecto.Modelos.Usuarios;


import org.example.proyecto.Modelos.Usuario;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioActual;

    private SesionUsuario() {}

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean isSesionActiva() {
        return usuarioActual != null;
    }

    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombreUsuario() : null;
    }

    public String getCargoUsuario() {
        return usuarioActual != null ? usuarioActual.getCargo() : null;
    }
}
