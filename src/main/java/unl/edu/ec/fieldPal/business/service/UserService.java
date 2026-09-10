package unl.edu.ec.fieldPal.business.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.domain.User;
import unl.edu.ec.fieldPal.domain.enums.UserRole;
import unl.edu.ec.fieldPal.business.repository.UserRepository;
import unl.edu.ec.fieldPal.exception.AlreadyEntityException;
import unl.edu.ec.fieldPal.exception.CredentialInvalidException;
import unl.edu.ec.fieldPal.exception.EncryptorException;
import unl.edu.ec.fieldPal.exception.EntityNotFoundException;
import unl.edu.ec.fieldPal.util.security.EncryptorManager;

@Named
@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    public UserService() {
    }

    public User authenticate(String email, String password)
            throws CredentialInvalidException {
        try {
            User userFound = userRepository.findByEmail(email);
            String pwdEncrypted = EncryptorManager.encrypt(password);
            if (userFound.getPassword().equals(pwdEncrypted)) {
                return userFound;
            }
            throw new CredentialInvalidException();
        } catch (EncryptorException e) {
            throw new CredentialInvalidException("Credenciales incorrectas", e);
        }
    }

    public User register(String name, String email, String phone, String password, UserRole role) throws EncryptorException, AlreadyEntityException {
        if (email == null) return null;
        User user = new User(null, name, email, phone, password, role);

        String pwdEncrypted = EncryptorManager.encrypt(user.getPassword());
        user.setPassword(pwdEncrypted);
        // Regla Negocio => El nombre de usuario debe ser unico
        try {
            User userFound = userRepository.find(user.getName());
        } catch (EntityNotFoundException e) {
            return userRepository.save(user);
        }
        throw new AlreadyEntityException("Ya existe otro usuario con ese nombre");
    }

    public void updateUser(User user, String rawNewPassword) throws EncryptorException, AlreadyEntityException {
        if (rawNewPassword != null && !rawNewPassword.isEmpty()) {
            String pwdEncrypted = EncryptorManager.encrypt(rawNewPassword);
            user.setPassword(pwdEncrypted);
        }
        // Regla Negocio => El nombre de usuario debe ser único
        try {
            User userFound = userRepository.find(user.getName());
            if (!userFound.getId().equals(user.getId())) {
                throw new AlreadyEntityException("Ya existe otro usuario con ese nombre");
            }
        } catch (EntityNotFoundException e) {
            // No hay conflicto, se puede continuar
        }
        userRepository.save(user);
    }

    public int getUserCount() {
        return (int) userRepository.count();
    }
}