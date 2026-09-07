package unl.edu.ec.fieldPal.controller;

import unl.edu.ec.fieldPal.domain.Court;
import unl.edu.ec.fieldPal.domain.Organization;
import unl.edu.ec.fieldPal.domain.Reservation;
import unl.edu.ec.fieldPal.domain.enums.CourtType;
import unl.edu.ec.fieldPal.domain.enums.ReservationStatus;
import unl.edu.ec.fieldPal.domain.enums.Zone;
import unl.edu.ec.fieldPal.business.service.CourtService;
import unl.edu.ec.fieldPal.business.service.OrganizationService;
import unl.edu.ec.fieldPal.business.service.ReservationService;
import unl.edu.ec.fieldPal.business.service.UserService;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.faces.FacesUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author NeoCoreTeam
 * Managed Bean para el panel de administración
 */

@Named
@ViewScoped
public class GestionBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private CourtService courtService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private UserService userService;

    @Inject
    private AuthBean authBean;

    // === Tab activo ===
    private String activeTab = "dashboard";

    // === Búsqueda ===
    private String search = "";

    // === Formulario nueva organización ===
    private String newOrgName = "";
    private Zone newOrgZone = Zone.NORTE;
    private String newOrgAddress = "";
    private String newOrgPhone = "";
    private String newOrgDesc = "";
    private double newOrgLatitude = 0.0;
    private double newOrgLongitude = 0.0;

    // === Formulario nueva cancha ===
    private Long newCourtOrg = null;
    private String newCourtName = "";
    private CourtType newCourtType = CourtType.FUTBOL;
    private double newCourtPrice = 0;
    private String newCourtSurface = "";
    private boolean newCourtLighting = true;
    private boolean newCourtCovered = false;

    // === Edición de cancha ===
    private Court editingCourt;
    private boolean showEditCourtModal = false;

    // === Edición de organización ===
    private Organization editingOrg;
    private boolean showEditOrgModal = false;

    // === Reportes ===
    private String reportDate = "2026-07-14";

    // === Listas para combos del formulario ===
    public Zone[] getZones() { return Zone.values(); }
    public CourtType[] getCourtTypes() { return CourtType.values(); }

    // === Dashboard stats ===
    // Cálculo sobre lo que pertenece a la organización del admin logueado.
    public int getTotalCanchas() { return getAllCourts().size(); }

    public int getReservasActivas() {
        return (int) getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.UPCOMING)
                .count();
    }

    public int getUsuarios() { return userService.getUserCount(); }

    public double getIngresosMes() {
        return getAllReservations().stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .mapToDouble(Reservation::getTotalPrice)
                .sum();
    }

    // === Organizaciones ===
    // Un admin solo debe ver/gestionar SU propia organización, no las de otros admins.
    public List<Organization> getAllOrganizations() {
        Organization mine = getMyOrganization();
        return mine != null ? List.of(mine) : List.of();
    }

    public String doAddOrganization() {
        // TODO: Implementar registro real con BD
        Organization org = new Organization();
        org.setName(newOrgName);
        org.setZone(newOrgZone);
        org.setAddress(newOrgAddress);
        org.setPhone(newOrgPhone);
        org.setDescription(newOrgDesc);
        org.setRating(4.5);
        org.setLatitude(newOrgLatitude);
        org.setLongitude(newOrgLongitude);
        organizationService.addOrganization(org);

        clearOrgForm();
        FacesUtil.addSuccessMessage("Organización registrada exitosamente.");
        return null;
    }

    public void editOrganization(Organization org) {
        this.editingOrg = org;
        this.showEditOrgModal = true;
    }

    public String doUpdateOrganization() {
        if (editingOrg != null) {
            organizationService.updateOrganization(editingOrg);
            showEditOrgModal = false;
            editingOrg = null;
            FacesUtil.addSuccessMessage("Organización actualizada.");
        }
        return null;
    }

    public void removeOrganization(Organization org) {
        organizationService.removeOrganization(org);
        FacesUtil.addSuccessMessage("Organización eliminada.");
    }

    // === Canchas ===
    // Se filtra por la organización del admin logueado.
    public List<Court> getAllCourts() {
        return courtService.getByOrg(authBean.getOrganizationId());
    }

    public String doAddCourt() {
        if (newCourtOrg == null) {
            FacesUtil.addErrorMessage("Selecciona una organización.");
            return null;
        }
        Court court = new Court();
        court.setOrgId(newCourtOrg);

        court.setName(newCourtName);
        court.setType(newCourtType);
        court.setPricePerHour(newCourtPrice);
        court.setSurface(newCourtSurface);
        court.setHasLighting(newCourtLighting);
        court.setCovered(newCourtCovered);
        courtService.addCourt(court);

        clearCourtForm();
        FacesUtil.addSuccessMessage("Cancha registrada exitosamente.");
        return null;
    }

    public void editCourt(Court court) {
        this.editingCourt = court;
        this.showEditCourtModal = true;
    }

    public String doUpdateCourt() {
        if (editingCourt != null) {
            courtService.updateCourt(editingCourt);
            showEditCourtModal = false;
            editingCourt = null;
            FacesUtil.addSuccessMessage("Cancha actualizada.");
        }
        return null;
    }

    public void removeCourt(Long courtId) {
        courtService.removeCourt(courtId);
        FacesUtil.addSuccessMessage("Cancha eliminada.");
    }

    // === Reservas ===
    // Antes: reservationService.getAll() devolvía TODAS las reservas del sistema,
    // incluyendo las del admin predefinido y las de cualquier otra organización.
    public List<Reservation> getAllReservations() {
        List<Reservation> all = reservationService.getByOrg(authBean.getOrganizationId());
        if (search == null || search.isEmpty()) return all;
        String lowerSearch = search.toLowerCase();
        return all.stream()
                .filter(r -> (r.getContactName() != null && r.getContactName().toLowerCase().contains(lowerSearch))
                        || getCourtName(r.getCourtId()).toLowerCase().contains(lowerSearch)
                        || getOrgName(r.getOrgId()).toLowerCase().contains(lowerSearch))
                .toList();
    }

    public void cancelReservation(Long reservationId) {
        reservationService.cancelReservation(reservationId);
        FacesUtil.addSuccessMessage("Reserva cancelada.");
    }

    // === Helpers ===
    public String getCourtName(Long courtId) {
        Court c = courtService.findById(courtId);
        return c != null ? c.getName() : "—";
    }

    public String getOrgName(Long orgId) {
        Organization o = organizationService.findById(orgId);
        return o != null ? o.getName() : "—";
    }

    public String getOrgZoneName(Long orgId) {
        Organization o = organizationService.findById(orgId);
        return o != null && o.getZone() != null ? o.getZone().getLabel() : "—";
    }

    // === Organización del admin logueado (para el saludo del panel) ===
    public Organization getMyOrganization() {
        Long orgId = authBean.getOrganizationId();
        if (orgId == null) return null;
        return organizationService.findById(orgId);
    }

    // === Limpiar formularios ===
    private void clearOrgForm() {
        newOrgName = "";
        newOrgZone = Zone.NORTE;
        newOrgAddress = "";
        newOrgPhone = "";
        newOrgDesc = "";
        newOrgLatitude = 0.0;
        newOrgLongitude = 0.0;
    }

    private void clearCourtForm() {
        newCourtOrg = null;
        newCourtName = "";
        newCourtType = CourtType.FUTBOL;
        newCourtPrice = 0;
        newCourtSurface = "";
        newCourtLighting = true;
        newCourtCovered = false;
    }

    // === Pestaña activa del panel ===
    // JSF resuelve #{gestionBean.activeTab} mediante estos métodos.
    public String getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(String activeTab) {
        this.activeTab = activeTab;
    }

    // === Texto de búsqueda de reservas ===
    // Requerido por #{gestionBean.search} en gestion.xhtml.
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
