package unl.edu.ec.fieldPal.controller;

import jakarta.annotation.PostConstruct;
import unl.edu.ec.fieldPal.domain.Court;
import unl.edu.ec.fieldPal.domain.Reservation;
import unl.edu.ec.fieldPal.domain.TimeSlot;
import unl.edu.ec.fieldPal.domain.enums.ReservationStatus;
import unl.edu.ec.fieldPal.business.service.CourtService;
import unl.edu.ec.fieldPal.business.service.ReservationService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.business.service.ScheduleService;
import unl.edu.ec.fieldPal.faces.FacesUtil;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author NeoCoreTeam
 * Managed Bean para la página de reserva.
 * Datos quemados - editar después para conectar a BD real.
 */

@Named
@ViewScoped
public class ReservaBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CourtService courtService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private AuthBean authBean;

    @PostConstruct
    public void init() {
        if (authBean.isAuthenticated()) {
            contactName = authBean.getCurrentUser().getName();
            contactPhone = authBean.getCurrentUser().getPhone();
        }
    }

    // Filtros
    private Long selectedCourtId = null;

    // Datos de reserva
    private LocalDate date = LocalDate.now();
    private LocalTime hour = null;
    private int duration = 1;
    private int playerCount = 5;
    private String contactName = "";
    private String contactPhone = "";

    // Estado
    private boolean submitted = false;

    public Court getActiveCourt() {
        if (selectedCourtId != null) {
            return courtService.findById(selectedCourtId);
        }
        return null;
    }

    public double getTotalPrice() {
        Court court = getActiveCourt();
        if (court == null) return 0;
        return court.getPricePerHour() * duration;
    }

    public double getPricePerPlayer() {
        int players = Math.max(playerCount, 1);
        return getTotalPrice() / players;
    }

    public List<LocalTime> getAvailableHours() {
        if (selectedCourtId == null || date == null) {
            return List.of();
        }
        return scheduleService.getSchedule(selectedCourtId, date).stream()
                .filter(TimeSlot::isAvailable)
                .map(TimeSlot::getHour)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<TimeSlot> getScheduleSlots() {
        if (selectedCourtId == null || date == null) {
            return List.of();
        }
        return scheduleService.getSchedule(selectedCourtId, date);
    }

    public void onDateOrCourtChange() {
        this.hour = null;
    }

    public String doReserve() {
        if (!authBean.isAuthenticated()) {
            // Hay faces-redirect=true hacia login.xhtml -> usamos "AndKeep"
            // para que el mensaje sobreviva al cambio de vista.
            FacesUtil.addErrorMessageAndKeep("Debes iniciar sesión para reservar.");
            return "/login.xhtml?faces-redirect=true";
        }

        // Validaciones básicas
        if (!getAvailableHours().contains(hour)) {
            FacesUtil.addErrorMessage("La hora seleccionada ya no está disponible.");
            return null;
        }
        if (selectedCourtId == null) {
            FacesUtil.addErrorMessage("Selecciona una cancha.");
            return null;
        }
        if (date == null) {
            FacesUtil.addErrorMessage("Selecciona una fecha.");
            return null;
        }
        if (hour == null) {
            FacesUtil.addErrorMessage("Selecciona una hora.");
            return null;
        }

        if (!getAvailableHours().contains(hour)) {
            FacesUtil.addErrorMessage("La hora seleccionada ya no está disponible.");
            return null;
        }

        Court court = getActiveCourt();
        if (court == null) {
            FacesUtil.addErrorMessage("Cancha no encontrada.");
            return null;
        }

        Reservation res = new Reservation();
        res.setUser(authBean.getCurrentUser());
        res.setOrganization(court.getOrganization());
        res.setCourt(court);
        res.setDate(date);
        res.setHour(hour);
        res.setDuration(duration);
        res.setPlayerCount(playerCount);
        res.setTotalPrice(getTotalPrice());
        res.setStatus(ReservationStatus.UPCOMING);
        res.setConfirmed(false);
        res.setContactName(contactName);
        res.setContactPhone(contactPhone);

        reservationService.addReservation(res);
        scheduleService.reserve(selectedCourtId, date, hour);
        submitted = true;

        // Hay faces-redirect=true hacia mis-reservas.xhtml -> "AndKeep"
        FacesUtil.addSuccessMessageAndKeep(
                "¡Reserva confirmada! Cancha: " + court.getName() +
                        " | Fecha: " + date + " " + hour);

        return "/mis-reservas.xhtml?faces-redirect=true";
    }

    // Getters y Setters
    public Long getSelectedCourtId() { return selectedCourtId; }
    public void setSelectedCourtId(Long selectedCourtId) { this.selectedCourtId = selectedCourtId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHour() { return hour; }
    public void setHour(LocalTime hour) { this.hour = hour; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
}