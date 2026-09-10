package unl.edu.ec.fieldPal.controller;

import unl.edu.ec.fieldPal.domain.Organization;
import unl.edu.ec.fieldPal.domain.enums.Zone;
import unl.edu.ec.fieldPal.business.repository.OrganizationRepository;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author NeoCoreTeam
 * Managed Bean para la página de inicio.
 */
@Named
@ViewScoped
public class HomeBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private OrganizationRepository organizationRepository;

    // Filtro de zona seleccionada
    private Zone selectedZone;

    // Datos de estadísticas
    private final int totalReservas = 3200;
    private final int totalCanchas = 24;
    private final int satisfaccion = 98;
    private final String atencion = "24/7";

    public List<Organization> getOrganizations() {
        if (selectedZone != null) {
            return organizationRepository.getByZone(selectedZone);
        }
        return organizationRepository.getAll();
    }

    public List<Zone> getAvailableZones() {
        return organizationRepository.getAvailableZones();
    }

    public void filterByZone(Zone zone) {
        this.selectedZone = zone;
    }

    public void clearZoneFilter() {
        this.selectedZone = null;
    }

    // Getters y Setters
    public Zone getSelectedZone() { return selectedZone; }
    public void setSelectedZone(Zone selectedZone) { this.selectedZone = selectedZone; }

    public int getTotalReservas() { return totalReservas; }
    public int getTotalCanchas() { return totalCanchas; }
    public int getSatisfaccion() { return satisfaccion; }
    public String getAtencion() { return atencion; }
}
