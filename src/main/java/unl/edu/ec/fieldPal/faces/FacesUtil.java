package unl.edu.ec.fieldPal.faces;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class FacesUtil {

    // Constructor privado: esta clase nunca debe instanciarse,
    // solo se usan sus métodos estáticos (patrón "utility class").
    private FacesUtil() {
    }

    // ---------------------------------------------------------------
    // Atajos de "éxito" (severidad INFO)
    // ---------------------------------------------------------------

    /**
     * Mensaje de éxito con resumen y detalle, visible solo en la vista actual.
     * Ej: FacesUtil.addSuccessMessage("Reserva", "Se creó correctamente.");
     */
    public static void addSuccessMessage(String summary, String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    /**
     * Igual que addSuccessMessage, pero el mensaje sobrevive a un
     * redirect (por ejemplo: guardar y luego navegar a otra página).
     */
    public static void addSuccessMessageAndKeep(String summary, String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    /** Mensaje de éxito solo con detalle (sin resumen). */
    public static void addSuccessMessage(String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, null, detail);
    }

    /** Mensaje de éxito solo con detalle, que sobrevive a un redirect. */
    public static void addSuccessMessageAndKeep(String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_INFO, null, detail);
    }

    // ---------------------------------------------------------------
    // Atajos de "error" (severidad ERROR)
    // ---------------------------------------------------------------

    /** Mensaje de error con resumen y detalle, visible solo en la vista actual. */
    public static void addErrorMessage(String summary, String detail) {
        addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    /** Igual que addErrorMessage, pero sobrevive a un redirect. */
    public static void addErrorMessageAndKeep(String summary, String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    /** Mensaje de error solo con detalle (sin resumen). */
    public static void addErrorMessage(String detail) {
        addMessage(FacesMessage.SEVERITY_ERROR, null, detail);
    }

    /** Mensaje de error solo con detalle, que sobrevive a un redirect. */
    public static void addErrorMessageAndKeep(String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_ERROR, null, detail);
    }

    // ---------------------------------------------------------------
    // Atajos de "advertencia" (severidad WARN)
    // Se incluyen porque WizardBean ya usa SEVERITY_WARN.
    // ---------------------------------------------------------------

    /** Mensaje de advertencia con resumen y detalle. */
    public static void addWarnMessage(String summary, String detail) {
        addMessage(FacesMessage.SEVERITY_WARN, summary, detail);
    }

    /** Mensaje de advertencia que sobrevive a un redirect. */
    public static void addWarnMessageAndKeep(String summary, String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_WARN, summary, detail);
    }

    // ---------------------------------------------------------------
    // Métodos base: todo lo anterior termina llamando a uno de estos dos
    // ---------------------------------------------------------------

    /**
     * Agrega un mensaje que solo se muestra en la vista actual
     * (se pierde si el bean hace un redirect después).
     *
     * @param severity nivel del mensaje: INFO, WARN o ERROR
     * @param summary  texto corto/resumen (puede ir null)
     * @param detail   texto detallado que verá el usuario
     */
    public static void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesMessage fm = new FacesMessage(severity, summary, detail);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, fm);
    }

    /**
     * Agrega un mensaje y lo marca para que "sobreviva" a la siguiente
     * vista (útil en flujos con redirect, ej: guardar -> redirigir a lista).
     * Internamente usa el Flash scope de JSF (getExternalContext().getFlash()).
     *
     * @param severity nivel del mensaje: INFO, WARN o ERROR
     * @param summary  texto corto/resumen (puede ir null)
     * @param detail   texto detallado que verá el usuario
     */
    public static void addMessageAndKeep(FacesMessage.Severity severity, String summary, String detail) {
        FacesMessage fm = new FacesMessage(severity, summary, detail);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, fm);
        // setKeepMessages(true) le dice al Flash scope que conserve
        // los mensajes agregados durante ESTA petición hacia la SIGUIENTE.
        facesContext.getExternalContext().getFlash().setKeepMessages(true);
    }
}