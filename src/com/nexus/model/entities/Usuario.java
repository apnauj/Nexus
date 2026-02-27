package Main;

public class Usuario {
    private UUID id;
    private String username;
    private String password;
    private String rol;

    Usuario(String username, String password, String rol) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}