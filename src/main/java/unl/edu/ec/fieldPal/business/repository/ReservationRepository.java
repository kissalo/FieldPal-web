package unl.edu.ec.fieldPal.business.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityNotFoundException;
import unl.edu.ec.fieldPal.domain.Reservation;
import unl.edu.ec.fieldPal.domain.enums.ReservationStatus;

import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class ReservationRepository {

    @Inject
    private unl.edu.ec.fieldPal.business.service.ReservationService reservationService;

    public ReservationRepository() {
    }

    public List<Reservation> getAll() {
        return reservationService.findAll();
    }

    public List<Reservation> getByUser(Long userId) {
        if (userId == null) return new ArrayList<>();
        return reservationService.findByUser(userId);
    }

    public List<Reservation> getByOrg(Long orgId) {
        if (orgId == null) return new ArrayList<>();
        return reservationService.findByOrg(orgId);
    }

    public Reservation findById(Long id) throws EntityNotFoundException {
        if (id == null) return null;
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) {
            throw new EntityNotFoundException("Reserva no encontrada con ID [" + id + "]");
        }
        return reservation;
    }

    public void addReservation(Reservation res) {
        if (res == null) return;
        reservationService.save(res);
    }

    public void cancelReservation(Long id) {
        Reservation res = findById(id);
        if (res != null) {
            res.setStatus(ReservationStatus.CANCELLED);
            reservationService.save(res);
        }
    }

    public void confirmReservation(Long id) {
        Reservation res = findById(id);
        if (res != null) {
            res.setConfirmed(true);
            reservationService.save(res);
        }
    }

    public void updateReservation(Reservation reservation) {
        if (reservation == null || reservation.getId() == null) return;
        reservationService.save(reservation);
    }

    public int getActiveCount() {
        return (int) reservationService.countByStatus(ReservationStatus.UPCOMING);
    }

    public double getMonthlyIncome() {
        return reservationService.sumTotalPriceExcluding(ReservationStatus.CANCELLED);
    }

}
