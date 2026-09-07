package unl.edu.ec.fieldPal.controller;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.domain.User;
import unl.edu.ec.fieldPal.domain.enums.UserRole;
import unl.edu.ec.fieldPal.business.service.UserService;
import unl.edu.ec.fieldPal.exception.AlreadyEntityException;
import unl.edu.ec.fieldPal.exception.CredentialInvalidException;
import unl.edu.ec.fieldPal.exception.EncryptorException;
import unl.edu.ec.fieldPal.faces.FacesUtil;
import unl.edu.ec.fieldPal.util.security.EncryptorManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author NeoCoreTeam
 */

@Named
@SessionScoped
public class AuthBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    private static final Logger logger = Logger.getLogger(AuthBean.class.getName());

    private User user;
    private Long selectedUserId;

    // Campos del formulario
    private String loginEmail = "";
    private String loginPassword = "";
    private String registerName = "";
    private String registerEmail = "";
    private String registerPhone = "";
    private String registerPassword = "";
    private String registerConfirmPassword = "";
    private String registerRole = "PLAYER";

    // === Validación ===
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    // Usuario actual en sesión
    private User currentUser;

    private Long organizationId;

    private User editableUser;

    public void loadUserForEdit() {
        if (currentUser == null) return;
        editableUser = new User(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getPhone(),
                currentUser.getPassword(), // sigue encriptada aquí
                currentUser.getRole()
        );
        decryptPassword(editableUser); // solo en esta copia temporal
    }

    public String cancelEdit() {
        editableUser = null; // se descarta, no vive más que el tiempo de edición
        return null;
    }

    // === Método de Login ===
    public String submitLogin() {
        loginEmail = trim(loginEmail);

        if (isBlank(loginEmail) || isBlank(loginPassword)) {
            addError("Ingresa tu correo y contraseña.");
            return null;
        }

        try {
            User user = userService.authenticate(loginEmail, loginPassword);
            loginSuccess(user, "¡Bienvenido, " + user.getName() + "!");
            clearLoginForm();
            return "/homepage.xhtml?faces-redirect=true";
        } catch (CredentialInvalidException e) {
            addError("Correo o contraseña incorrectos.");
            return null;
        }
    }

    // === Método de Registro ===
    public String submitRegister() {
        registerName = trim(registerName);
        registerEmail = trim(registerEmail);
        registerPhone = trim(registerPhone);

        if (isBlank(registerName) || registerName.length() < 2) {
            addError("Ingresa tu nombre completo.");
            return null;
        }
        if (isBlank(registerEmail) || !EMAIL_PATTERN.matcher(registerEmail).matches()) {
            addError("Ingresa un correo electrónico válido.");
            return null;
        }
        if (isBlank(registerPhone) || registerPhone.replaceAll("\\D", "").length() < 7) {
            addError("Ingresa un número de teléfono válido.");
            return null;
        }
        if (isBlank(registerPassword) || registerPassword.length() < MIN_PASSWORD_LENGTH) {
            addError("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
            return null;
        }
        if (!registerPassword.equals(registerConfirmPassword)) {
            addError("Las contraseñas no coinciden.");
            return null;
        }

        UserRole role = "ADMIN".equals(registerRole) ? UserRole.ADMIN : UserRole.PLAYER;

        try {
            User user = userService.register(registerName, registerEmail,
                    registerPhone, registerPassword, role);
            loginSuccess(user, "Cuenta creada exitosamente. ¡Bienvenido, " + user.getName() + "!");
            clearRegisterForm();
            return "/homepage.xhtml?faces-redirect=true";
        } catch (AlreadyEntityException e) {
            addError("Ya existe una cuenta con este nombre de usuario.");
            return null;
        } catch (EncryptorException e) {
            addError("Ocurrió un problema al procesar tu contraseña. Intenta nuevamente.");
            return null;
        }
    }

    // === Helpers de validación (evitan repetir la misma lógica en login/registro) ===
    private void loginSuccess(User user, String welcomeMessage) {
        currentUser = user;
        organizationId = user.isAdmin() ? user.getOrganizationId() : null;
        FacesUtil.addSuccessMessageAndKeep(welcomeMessage);
    }

    private void decryptPassword(User user) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                logger.info("Password no nulo y no vacío, procediendo a desencriptar");
                String pwdDecrypted = EncryptorManager.decrypt(user.getPassword());
                user.setPassword(pwdDecrypted);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Inconveniente al descifrar la clave", e);
            addError("Inconveniente al descifrar la clave: " + e.getMessage());
        }
    }

    private void addError(String detail) {
        FacesUtil.addErrorMessage(detail);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    // === Métodos de sesión ===
    public String doLogout() {
        currentUser = null;
        organizationId = null;
        return "/homepage.xhtml?faces-redirect=true";
    }

    // Alias para coincidir exactamente con el action de header.xhtml
    public String logout() {
        return doLogout();
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean isPlayer() {
        return currentUser != null && currentUser.isPlayer();
    }

    // === Limpiar formularios ===
    private void clearLoginForm() {
        loginEmail = "";
        loginPassword = "";
    }

    private void clearRegisterForm() {
        registerName = "";
        registerEmail = "";
        registerPhone = "";
        registerPassword = "";
        registerConfirmPassword = "";
        registerRole = "PLAYER";
    }

    // === Getters y Setters ===
    public String getLoginEmail() { return loginEmail; }
    public void setLoginEmail(String loginEmail) { this.loginEmail = loginEmail; }

    public String getLoginPassword() { return loginPassword; }
    public void setLoginPassword(String loginPassword) { this.loginPassword = loginPassword; }

    public String getRegisterName() { return registerName; }
    public void setRegisterName(String registerName) { this.registerName = registerName; }

    public String getRegisterEmail() { return registerEmail; }
    public void setRegisterEmail(String registerEmail) { this.registerEmail = registerEmail; }

    public String getRegisterPhone() { return registerPhone; }
    public void setRegisterPhone(String registerPhone) { this.registerPhone = registerPhone; }

    public String getRegisterPassword() { return registerPassword; }
    public void setRegisterPassword(String registerPassword) { this.registerPassword = registerPassword; }

    public String getRegisterConfirmPassword() { return registerConfirmPassword; }
    public void setRegisterConfirmPassword(String registerConfirmPassword) { this.registerConfirmPassword = registerConfirmPassword; }

    public String getRegisterRole() { return registerRole; }
    public void setRegisterRole(String registerRole) { this.registerRole = registerRole; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}
