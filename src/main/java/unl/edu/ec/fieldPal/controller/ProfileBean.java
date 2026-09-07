package unl.edu.ec.fieldPal.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.domain.User;
import unl.edu.ec.fieldPal.business.service.UserService;
import unl.edu.ec.fieldPal.exception.AlreadyEntityException;
import unl.edu.ec.fieldPal.exception.EncryptorException;
import unl.edu.ec.fieldPal.exception.EntityNotFoundException;

import java.io.Serial;
import java.io.Serializable;


/**
 * @author NeoCoreTeam
 */
@Named
@ViewScoped
public class ProfileBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    @Inject
    private AuthBean authBean;

    private User editingUser;
    private String newPassword = "";
    private String confirmPassword = "";

    public void loadUser() {
        User current = authBean.getCurrentUser();
        if (current == null) {
            editingUser = new User();
            return;
        }
        // Copia: no se modifica currentUser hasta que se guarde correctamente
        editingUser = new User(
                current.getId(),
                current.getName(),
                current.getEmail(),
                current.getPhone(),
                current.getPassword(), // sigue encriptada, nunca se muestra
                current.getRole()
        );
        newPassword = "";
        confirmPassword = "";
    }

    public void doUpdateProfile() {
        if (editingUser == null) {
            addError("No hay un usuario cargado.");
            return;
        }

        String rawNewPassword = null;
        boolean wantsPasswordChange = !isBlank(newPassword) || !isBlank(confirmPassword);
        if (wantsPasswordChange) {
            if (!newPassword.equals(confirmPassword)) {
                addError("Las contraseñas no coinciden.");
                return;
            }
            if (newPassword.length() < 6) {
                addError("La contraseña debe tener al menos 6 caracteres.");
                return;
            }
            rawNewPassword = newPassword;
        }

        try {
            userService.updateUser(editingUser, rawNewPassword);
            // Refresca la sesión con los datos actualizados (contraseña sigue encriptada)
            authBean.setCurrentUser(userService.findUser(editingUser.getId()));
            addInfo("Perfil actualizado correctamente.");
        } catch (AlreadyEntityException e) {
            addError("Ya existe otro usuario con ese nombre.");
        } catch (EncryptorException e) {
            addError("Ocurrió un problema al procesar la contraseña.");
        } catch (EntityNotFoundException e) {
            addError("No se pudo encontrar el usuario para actualizar.");
        }
    }

    private void addError(String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, detail, ""));
    }

    private void addInfo(String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, detail, ""));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public User getEditingUser() { return editingUser; }
    public void setEditingUser(User editingUser) { this.editingUser = editingUser; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}