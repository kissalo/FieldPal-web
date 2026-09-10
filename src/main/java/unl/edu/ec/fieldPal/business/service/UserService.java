package unl.edu.ec.fieldPal.business.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.ec.fieldPal.domain.User;
import unl.edu.ec.fieldPal.exception.EntityNotFoundException;
import unl.edu.ec.fieldPal.business.genericService.CrudGenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class UserService {

    @Inject
    private CrudGenericService crud;

    public User save(User user) {
        if (user.getId() == null || crud.find(User.class, user.getId()) == null) {
            return crud.create(user);
        }
        return crud.update(user);
    }

    public User findById(Long id) {
        if (id == null) return null;
        return crud.find(User.class, id);
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        return crud.findSingleResultOrNull(
                "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)", User.class, params);
    }

    /**
     * Login vía la named query "User.login" (email + password), tal como
     * antes se invocaba directamente desde UserService con CrudGenericService.
     */
    public User login(String email, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        return crud.findSingleResultOrNullWithNamedQuery("User.login", params);
    }

    public User find(String name) throws EntityNotFoundException {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        User userFound = crud.findSingleResultOrNullWithNamedQuery("User.findLikeName", params);
        if (userFound == null) {
            throw new EntityNotFoundException("User no encontrado con [" + name + "]");
        }
        return userFound;
    }

    public List<User> findAll() {
        return crud.findWithQuery("SELECT u FROM User u");
    }

    public long count() {
        return crud.count("SELECT COUNT(u) FROM User u");
    }
}
