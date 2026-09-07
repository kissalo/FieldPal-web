package unl.edu.ec.fieldPal.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import unl.edu.ec.fieldPal.domain.User;
import unl.edu.ec.fieldPal.business.service.UserService;
import unl.edu.ec.fieldPal.faces.FacesUtil;

/**
 * @author NeoCoreTeam
 */

@Named
@ViewScoped
public class ProfileBean implements Serializable {
    @Inject private AuthBean authBean;
    @Inject private UserService userService;
    private User editingUser;
    private String newPassword;
    private String confirmPassword;

    @Serial
    private static final long serialVersionUID = 1L;

    @PostConstruct
    public void init() {
        if (authBean.isAuthenticated()) {
            User current = authBean.getCurrentUser();
            // Clonamos el usuario para no alterar la sesión hasta que se guarde
            this.editingUser = new User(
                    current.getId(),
                    current.getName(),
                    current.getEmail(),
                    current.getPhone(),
                    current.getPassword(),
                    current.getRole()
            );
        }
    }

    public void doUpdateProfile() {
        try {
            // Lógica de cambio de contraseña SIN ENCRIPTACIÓN
            if (newPassword != null && !newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    FacesUtil.addErrorMessage("Las contraseñas no coinciden.");
                    return;
                }
                // Guardamos directo para que el Login (que no usa encriptación) funcione
                editingUser.setPassword(newPassword);
            }

            // Persistencia en el servicio
            userService.updateUser(editingUser);

            // Sincronizar sesión actual para actualizar el Header
            authBean.setCurrentUser(editingUser);

            FacesUtil.addSuccessMessage("Tu perfil ha sido actualizado.");

            // Limpiar campos temporales
            this.newPassword = null;
            this.confirmPassword = null;

        } catch (Exception e) {
            FacesUtil.addErrorMessage("Error al guardar: " + e.getMessage());
        }
    }

    private void showMsg(FacesMessage.Severity s, String m) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, m, ""));
    }

    // Getters y Setters

    public User getEditingUser() { return editingUser; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String p) { this.newPassword = p; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String p) { this.confirmPassword = p; }
}
