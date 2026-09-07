package unl.edu.ec.fieldPal.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.domain.Organization;
import unl.edu.ec.fieldPal.domain.Court;
import unl.edu.ec.fieldPal.domain.enums.Zone;
import unl.edu.ec.fieldPal.domain.enums.CourtType;
import unl.edu.ec.fieldPal.domain.User;
import unl.edu.ec.fieldPal.business.service.OrganizationService;
import unl.edu.ec.fieldPal.business.service.CourtService;
import unl.edu.ec.fieldPal.business.service.UserService;
import unl.edu.ec.fieldPal.faces.FacesUtil;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NeoCoreTeam
 * Controller para gestionar el asistente (Wizard) de registro de nuevos complejos deportivos.
 * Lógica simplificada de validaciones apoyada en la capa de la Vista (XHTML).
 */
@Named("wizardBean")
@ViewScoped
public class WizardBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private CourtService courtService;

    @Inject
    private UserService userService;

    @Inject
    private AuthBean authBean;

    // Modelos principales del flujo
    private Organization newOrganization;
    private List<Court> tempCourts;
    private Court currentCourt;

    private boolean editMode = false;

    // Políticas y Horarios
    private List<ScheduleDay> scheduleDays;
    private Integer reservationDepositPercentage;
    private boolean allowFreeCancellation;

    @PostConstruct
    public void init() {
        Long existingOrgId = authBean.getOrganizationId();
        if (existingOrgId != null) {
            Organization existing = organizationService.findById(existingOrgId);
            if (existing != null) {
                newOrganization = existing;
                editMode = true;
                // Precargar las canchas que ya había registrado para este complejo
                tempCourts = new ArrayList<>();
                for (Court c : courtService.getAll()) {
                    if (existingOrgId.equals(c.getOrgId())) {
                        tempCourts.add(c);
                    }
                }
            }
        }

        // Si no había nada que precargar, se parte de cero como antes
        if (newOrganization == null) {
            newOrganization = new Organization();
        }
        if (tempCourts == null) {
            tempCourts = new ArrayList<>();
        }
        prepareNewCourt();

        // Inicializar políticas por defecto
        reservationDepositPercentage = 50;
        allowFreeCancellation = true;

        // Inicializar el configurador de horarios de 7 días
        scheduleDays = new ArrayList<>();
        scheduleDays.add(new ScheduleDay("Lunes", LocalTime.of(8, 0), LocalTime.of(22, 0), true));
        scheduleDays.add(new ScheduleDay("Martes", LocalTime.of(8, 0), LocalTime.of(22, 0), true));
        scheduleDays.add(new ScheduleDay("Miércoles", LocalTime.of(8, 0), LocalTime.of(22, 0), true));
        scheduleDays.add(new ScheduleDay("Jueves", LocalTime.of(8, 0), LocalTime.of(22, 0), true));
        scheduleDays.add(new ScheduleDay("Viernes", LocalTime.of(8, 0), LocalTime.of(23, 0), true));
        scheduleDays.add(new ScheduleDay("Sábado", LocalTime.of(8, 0), LocalTime.of(23, 0), true));
        scheduleDays.add(new ScheduleDay("Domingo", LocalTime.of(9, 0), LocalTime.of(21, 0), false));
    }

    // =========================================================================
    // LÓGICA DE VALIDACIÓN DE PASOS EN JAVA (REGULACIÓN DE NEGOCIO)
    // =========================================================================

    /**
     * Intercepta el flujo del Wizard para validar reglas lógicas de negocio antes de cambiar de sección.
     */
    public String onFlowProcess(org.primefaces.event.FlowEvent event) {
        String currentStep = event.getOldStep();
        String nextStep = event.getNewStep();

        // Permitir retroceder sin volver a disparar validaciones de negocio
        if (isGoingBackwards(currentStep, nextStep)) {
            return nextStep;
        }

        // VALIDACIÓN PASO 2: Canchas obligatorias
        if ("canchas".equals(currentStep)) {
            if (tempCourts == null || tempCourts.isEmpty()) {
                showError("Sin Canchas Registradas", "Debe registrar al menos una cancha para su complejo deportivo.");
                return currentStep; // Bloquea y se mantiene en "canchas"
            }
        }

        // Horarios coherentes de apertura/cierre
        if ("horarios".equals(currentStep)) {
            boolean alMenosUnDiaActivo = false;

            for (ScheduleDay day : scheduleDays) {
                if (day.isActive()) {
                    alMenosUnDiaActivo = true;

                    if (day.getOpenTime() != null && day.getCloseTime() != null) {
                        if (!day.getOpenTime().isBefore(day.getCloseTime())) {
                            showError("Inconsistencia en Horarios", "El horario configurado para el día "
                                    + day.getDayName() + " no es válido. La hora de apertura debe ser anterior a la de cierre.");
                            return currentStep;
                        }
                    } else {
                        showError("Horario Incompleto", "Por favor, defina la hora de apertura y de cierre para el " + day.getDayName());
                        return currentStep;
                    }
                }
            }

            if (!alMenosUnDiaActivo) {
                showError("Horario Semanal Vacío", "Debe definir al menos un día de la semana para atención al público.");
                return currentStep;
            }
        }

        return nextStep;
    }

    private boolean isGoingBackwards(String currentStep, String nextStep) {
        int currentIndex = getStepIndex(currentStep);
        int nextIndex = getStepIndex(nextStep);
        return nextIndex < currentIndex;
    }

    private int getStepIndex(String step) {
        return switch (step) {
            case "organizacion" -> 1;
            case "canchas" -> 2;
            case "horarios" -> 3;
            case "politicas" -> 4;
            default -> 99;
        };
    }

    // === Lógica del Paso 2 (Canchas) ===

    public void prepareNewCourt() {
        currentCourt = new Court();
        currentCourt.setType(CourtType.FUTBOL);
        currentCourt.setPricePerHour(15.0);
        currentCourt.setSurface("Césped Sintético");
        currentCourt.setHasLighting(true);
        currentCourt.setCovered(false);
    }

    public void saveTempCourt() {
        if (currentCourt != null && currentCourt.getName() != null && !currentCourt.getName().trim().isEmpty()) {

            if (currentCourt.getPricePerHour() <= 0) {
                showError("Precio Inválido", "El precio por hora de la cancha debe ser mayor que 0.");
                return;
            }

            tempCourts.add(currentCourt);

            FacesUtil.addSuccessMessage("Cancha '" + currentCourt.getName() + "' añadida exitosamente.");

            prepareNewCourt();
        } else {
            showError("Campo Requerido", "El nombre de la cancha es obligatorio.");
        }
    }

    public void removeTempCourt(Court court) {
        if (tempCourts.remove(court)) {
            FacesUtil.addWarnMessage(null, "La cancha '" + court.getName() + "' ha sido quitada.");
        }
    }

    // Lógica de Guardado Final

    public String saveAll() {
        try {
            if (newOrganization == null || tempCourts.isEmpty()) {
                showError("Registro Incompleto", "No es posible proceder. Faltan datos esenciales de la organización.");
                return null;
            }

            // 1. Guardar Organización
            organizationService.save(newOrganization);

            authBean.setOrganizationId(newOrganization.getId());

            // Persistir el vínculo admin -> organización en BD (no solo en la sesión),
            // así la próxima vez que este admin inicie sesión (incluso en otro navegador)
            // el sistema sabe cuál es SU organización y no mezcla datos con otros admins.
            User currentUser = authBean.getCurrentUser();
            if (currentUser != null) {
                currentUser.setOrganizationId(newOrganization.getId());
                userService.updateUser(currentUser);
            }

            // 2. Asociar y guardar canchas
            for (Court court : tempCourts) {
                court.setOrganizationId(newOrganization.getId());
                courtService.save(court);
            }

            // Hay faces-redirect=true hacia gestion.xhtml -> usamos "AndKeep",
            // que ya incluye internamente el setKeepMessages(true) que antes
            // se llamaba a mano en la línea de abajo.
            FacesUtil.addSuccessMessageAndKeep("¡Excelente!", editMode
                    ? "Los cambios de '" + newOrganization.getName() + "' se guardaron exitosamente."
                    : "El complejo '" + newOrganization.getName() + "' ha sido publicado exitosamente.");

            return "/admin/gestion.xhtml?faces-redirect=true";

        } catch (Exception e) {
            showError("Error de Persistencia", "No se pudo guardar la información: " + e.getMessage());
            return null;
        }
    }

    private void showError(String summary, String detail) {
        FacesUtil.addErrorMessage(summary, detail);
    }

    public Zone[] getZones() { return Zone.values(); }
    public CourtType[] getCourtTypes() { return CourtType.values(); }

    public boolean isEditMode() { return editMode; }

    // Getters y Setters

    public Organization getNewOrganization() { return newOrganization; }
    public void setNewOrganization(Organization newOrganization) { this.newOrganization = newOrganization; }

    public List<Court> getTempCourts() { return tempCourts; }
    public void setTempCourts(List<Court> tempCourts) { this.tempCourts = tempCourts; }

    public Court getCurrentCourt() { return currentCourt; }
    public void setCurrentCourt(Court currentCourt) { this.currentCourt = currentCourt; }

    public List<ScheduleDay> getScheduleDays() { return scheduleDays; }
    public void setScheduleDays(List<ScheduleDay> scheduleDays) { this.scheduleDays = scheduleDays; }

    public Integer getReservationDepositPercentage() { return reservationDepositPercentage; }
    public void setReservationDepositPercentage(Integer reservationDepositPercentage) { this.reservationDepositPercentage = reservationDepositPercentage; }

    public boolean isAllowFreeCancellation() { return allowFreeCancellation; }
    public void setAllowFreeCancellation(boolean allowFreeCancellation) { this.allowFreeCancellation = allowFreeCancellation; }

    // Clase Auxiliar: Representa el horario de un día individual de la semana
    public static class ScheduleDay implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String dayName;
        private LocalTime openTime;
        private LocalTime closeTime;
        private boolean active;

        public ScheduleDay(String dayName, LocalTime openTime, LocalTime closeTime, boolean active) {
            this.dayName = dayName;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.active = active;
        }

        public String getDayName() { return dayName; }
        public void setDayName(String dayName) { this.dayName = dayName; }

        public LocalTime getOpenTime() { return openTime; }
        public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }

        public LocalTime getCloseTime() { return closeTime; }
        public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }
}