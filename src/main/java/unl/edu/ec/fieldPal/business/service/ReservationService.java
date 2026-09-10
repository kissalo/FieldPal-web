package unl.edu.ec.fieldPal.business.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.ec.fieldPal.domain.Reservation;
import unl.edu.ec.fieldPal.domain.enums.ReservationStatus;
import unl.edu.ec.fieldPal.business.genericService.CrudGenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ReservationService {

    @Inject
    private CrudGenericService crud;

    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null || crud.find(Reservation.class, reservation.getId()) == null) {
            return crud.create(reservation);
        }
        return crud.update(reservation);
    }

    public Reservation findById(Long id) {
        if (id == null) return null;
        return crud.find(Reservation.class, id);
    }

    public List<Reservation> findAll() {
        return crud.findWithQuery("SELECT r FROM Reservation r");
    }

    public List<Reservation> findByUser(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return crud.findWithQuery("SELECT r FROM Reservation r WHERE r.user.id = :userId", params);
    }

    public List<Reservation> findByOrg(Long orgId) {
        if (orgId == null) return List.of();
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", orgId);
        return crud.findWithQuery("SELECT r FROM Reservation r WHERE r.organization.id = :orgId", params);
    }

    public long countByStatus(ReservationStatus status) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        return crud.count("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status", params);
    }

    public double sumTotalPriceExcluding(ReservationStatus excludedStatus) {
        return findAll().stream()
                .filter(r -> r.getStatus() != excludedStatus)
                .mapToDouble(Reservation::getTotalPrice)
                .sum();
    }
}
