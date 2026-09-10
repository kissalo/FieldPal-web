package unl.edu.ec.fieldPal.business.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityNotFoundException;
import unl.edu.ec.fieldPal.domain.Organization;
import unl.edu.ec.fieldPal.domain.enums.Zone;

import java.util.List;

@Named
@ApplicationScoped
public class OrganizationRepository {

    @Inject
    private unl.edu.ec.fieldPal.business.service.OrganizationService organizationService;

    public OrganizationRepository() {
    }

    public List<Organization> getAll() {
        return organizationService.findAll();
    }

    public List<Organization> getByZone(Zone zone) {
        if (zone == null) return getAll();
        return organizationService.findByZone(zone);
    }

    public Organization findById(Long id) throws EntityNotFoundException {
        Organization organization = organizationService.findById(id);
        if (organization == null){
            throw new EntityNotFoundException("Organization no encontrada con [" + id + "]");
        }
        return organization;
    }

    public Organization save(Organization organization) {
        if (organization == null) return null;
        return organizationService.save(organization);
    }

    public void addOrganization(Organization org) {
        if (org == null) return;
        organizationService.save(org);
    }

    public void updateOrganization(Organization org) {
        if (org == null || org.getId() == null) return;
        organizationService.save(org);
    }

    public void removeOrganization(Organization org) {
        if (org == null || org.getId() == null) return;
        organizationService.deleteById(org.getId());
    }

    public List<Zone> getAvailableZones() {
        return organizationService.findDistinctZones();
    }
}

