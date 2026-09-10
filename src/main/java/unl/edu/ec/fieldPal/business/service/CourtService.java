package unl.edu.ec.fieldPal.business.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.ec.fieldPal.domain.Court;
import unl.edu.ec.fieldPal.domain.enums.CourtType;
import unl.edu.ec.fieldPal.business.genericService.CrudGenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CourtService {

    @Inject
    private CrudGenericService crud;

    public Court save(Court court) {
        if (court.getId() == null || crud.find(Court.class, court.getId()) == null) {
            return crud.create(court);
        }
        return crud.update(court);
    }

    public Court findById(Long id) {
        if (id == null) return null;
        return crud.find(Court.class, id);
    }

    public List<Court> findAll() {
        return crud.findWithQuery("SELECT c FROM Court c");
    }

    public List<Court> findByOrg(Long orgId) {
        if (orgId == null) return List.of();
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", orgId);
        return crud.findWithQuery("SELECT c FROM Court c WHERE c.organization.id = :orgId", params);
    }

    public List<Court> findByType(CourtType type) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        return crud.findWithQuery("SELECT c FROM Court c WHERE c.type = :type", params);
    }

    public void deleteById(Long id) {
        crud.delete(Court.class, id);
    }

    public long count() {
        return crud.count("SELECT COUNT(c) FROM Court c");
    }
}
