package unl.edu.ec.fieldPal.business.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.ec.fieldPal.domain.TimeSlot;
import unl.edu.ec.fieldPal.business.genericService.CrudGenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class TimeSlotService {

    @Inject
    private CrudGenericService crud;

    public TimeSlot save(TimeSlot slot) {
        return crud.create(slot);
    }

    public List<java.time.LocalTime> findReservedHours(Long courtId, java.time.LocalDate date) {
        Map<String, Object> params = new HashMap<>();
        params.put("courtId", courtId);
        params.put("date", date);
        return crud.findWithQuery(
                "SELECT t.hour FROM TimeSlot t WHERE t.court.id = :courtId AND t.date = :date", params);
    }

    public long count() {
        return crud.count("SELECT COUNT(t) FROM TimeSlot t");
    }
}
