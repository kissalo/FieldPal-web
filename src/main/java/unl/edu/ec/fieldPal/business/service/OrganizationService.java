package unl.edu.ec.fieldPal.business.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.ec.fieldPal.domain.Organization;
import unl.edu.ec.fieldPal.domain.enums.Zone;
import unl.edu.ec.fieldPal.business.genericService.CrudGenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class OrganizationService {

    @Inject
    private CrudGenericService crud;

    public Organization save(Organization org) {
        if (org.getId() == null || crud.find(Organization.class, org.getId()) == null) {
            return crud.create(org);
        }
        return crud.update(org);
    }

    public Organization findById(Long id) {
        if (id == null) return null;
        return crud.find(Organization.class, id);
    }

    public List<Organization> findAll() {
        return crud.findWithQuery("SELECT o FROM Organization o");
    }

    public List<Organization> findByZone(Zone zone) {
        Map<String, Object> params = new HashMap<>();
        params.put("zone", zone);
        return crud.findWithQuery("SELECT o FROM Organization o WHERE o.zone = :zone", params);
    }

    public List<Zone> findDistinctZones() {
        return crud.findWithQuery("SELECT DISTINCT o.zone FROM Organization o");
    }

    public void deleteById(Long id) {
        crud.delete(Organization.class, id);
    }

    public long count() {
        return crud.count("SELECT COUNT(o) FROM Organization o");
    }
}
