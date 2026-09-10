package unl.edu.ec.fieldPal.controller;

import unl.edu.ec.fieldPal.domain.Court;
import unl.edu.ec.fieldPal.domain.Organization;
import unl.edu.ec.fieldPal.domain.TimeSlot;
import unl.edu.ec.fieldPal.domain.enums.Zone;
import unl.edu.ec.fieldPal.business.repository.CourtRepository;
import unl.edu.ec.fieldPal.business.repository.OrganizationRepository;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.business.repository.ScheduleRepository;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author NeoCoreTeam
 * Managed Bean para la página de consulta de horarios.
 */
@Named
@ViewScoped
public class HorariosBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private CourtRepository courtRepository;

    @Inject
    private ScheduleRepository scheduleRepository;

    // Filtros
    private Zone selectedZone;
    private Long selectedOrgId = null;
    private Long selectedCourtId = null;
    private LocalDate date = LocalDate.now();

    @PostConstruct
    public void init() {
        List<Organization> orgs = organizationRepository.getAll();
        if (!orgs.isEmpty() && orgs.get(0).getId() != null) {
            selectOrganization(orgs.get(0).getId());
        }
    }

    public List<Organization> getFilteredOrgs() {
        if (selectedZone != null) {
            return organizationRepository.getByZone(selectedZone);
        }
        return organizationRepository.getAll();
    }

    public List<Zone> getAvailableZones() {
        return organizationRepository.getAvailableZones();
    }

    public List<Court> getCourtsForSelectedOrg() {
        if (selectedOrgId == null) return List.of();
        return courtRepository.getByOrg(selectedOrgId);
    }

    public Court getActiveCourt() {
        if (selectedCourtId == null) return null;
        return courtRepository.findById(selectedCourtId);
    }

    public List<TimeSlot> getActiveSchedule() {
        if (selectedCourtId == null) return List.of();
        return scheduleRepository.getSchedule(selectedCourtId, date);
    }

    public void filterByZone(Zone zone) {
        this.selectedZone = zone;
        List<Organization> filtered = getFilteredOrgs();
        selectedOrgId = null;
        selectedCourtId = null;
        if (!filtered.isEmpty()) {
            selectOrganization(filtered.get(0).getId());
        }
    }

    public void clearZoneFilter() {
        this.selectedZone = null;
        List<Organization> orgs = organizationRepository.getAll();
        selectedOrgId = null;
        selectedCourtId = null;
        if (!orgs.isEmpty()) {
            selectOrganization(orgs.get(0).getId());
        }
    }

    public void selectOrganization(Long orgId) {
        this.selectedOrgId = orgId;
        List<Court> courts = courtRepository.getByOrg(orgId);
        this.selectedCourtId = courts.isEmpty() ? null : courts.get(0).getId();
    }

    public void selectCourt(Long courtId) {
        this.selectedCourtId = courtId;
    }

    // Getters y Setters
    public Zone getSelectedZone() { return selectedZone; }
    public void setSelectedZone(Zone selectedZone) { this.selectedZone = selectedZone; }

    public Long getSelectedOrgId() { return selectedOrgId; }
    public void setSelectedOrgId(Long selectedOrgId) { this.selectedOrgId = selectedOrgId; }

    public Long getSelectedCourtId() { return selectedCourtId; }
    public void setSelectedCourtId(Long selectedCourtId) { this.selectedCourtId = selectedCourtId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}