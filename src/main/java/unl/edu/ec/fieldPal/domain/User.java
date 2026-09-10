package unl.edu.ec.fieldPal.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import unl.edu.ec.fieldPal.domain.enums.UserRole;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa a un usuario
 * Se utiliza "users" en plural porque "user" es una palabra reservada en PostgreSQL
 */
@Entity
@Table(name = "users")
@NamedQueries({
        // Consulta para el login: busca un usuario por email y contraseña.
        // Ventaja: Precompilada por JPA, más segura contra SQL Injection.
        @NamedQuery(name = "User.login",
                query = "SELECT u FROM User u WHERE u.email = :email AND u.password = :password"),

        // Consulta para obtener todos los usuarios
        @NamedQuery(name = "User.findAll",
                query = "SELECT u FROM User u"),

        // Consulta para buscar un usuario por su correo único.
        @NamedQuery(name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"),

        //Consulta para buscar un usuario por su nombre.
        @NamedQuery( name = "User.findLikeName",
                query = "SELECT u FROM User u WHERE u.name LIKE :name")
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Ingrese una dirección de correo válida")
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @NotNull(message = "Debe asignar un rol al usuario")
    private UserRole role; // PLAYER o ADMIN. [2]

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Relación con la Organización que administra (solo aplica a usuarios con rol ADMIN).
     * Es la que faltaba: sin este vínculo persistido en BD, no había forma de saber
     * qué organización/canchas/reservas le pertenecen a cada admin, y todos los
     * paneles terminaban mostrando los datos de TODOS los admins mezclados.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", referencedColumnName = "id", nullable = true)
    private Organization organization;

    /**
     * Relación con Reservas: Un usuario puede tener muchas reservas.
     * Mapeada por el atributo 'user' en la clase Reservation.
     * */
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    // Constructor por defecto requerido por JPA
    public User() {}

    /**
     * Constructor completo para inicialización y clonación de perfiles.
     * Ajustado para recibir Long como ID.
     */
    public User(Long id, String name, String email, String phone, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public User(String name, String email, String phone, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // === Métodos de Lógica de Negocio (Helpers) ===

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isPlayer() {
        return role == UserRole.PLAYER;
    }

    // === Getters y Setters ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public Long getOrganizationId() {
        return organization != null ? organization.getId() : null;
    }

    public void setOrganizationId(Long organizationId) {
        if (organizationId == null) {
            this.organization = null;
            return;
        }
        if (this.organization == null) {
            this.organization = new Organization();
        }
        this.organization.setId(organizationId);
    }

    // === Implementación de Equals y HashCode (Comparación por ID) ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
