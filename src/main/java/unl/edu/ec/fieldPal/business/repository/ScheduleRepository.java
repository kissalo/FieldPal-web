package unl.edu.ec.fieldPal.business.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.fieldPal.domain.Court;
import unl.edu.ec.fieldPal.domain.TimeSlot;
import unl.edu.ec.fieldPal.business.service.CourtService;
import unl.edu.ec.fieldPal.business.service.TimeSlotService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class ScheduleRepository {

    @Inject
    private TimeSlotService timeSlotService;

    @Inject
    private CourtService courtService;

    public ScheduleRepository() {
    }

    public List<TimeSlot> getSchedule(Long courtId, LocalDate date) {
        if (courtId == null || date == null) return new ArrayList<>();

        Court court = courtService.findById(courtId);
        List<LocalTime> reservedHours = timeSlotService.findReservedHours(courtId, date);

        List<TimeSlot> slots = new ArrayList<>();
        for (int h = 8; h <= 22; h++) {
            LocalTime time = LocalTime.of(h, 0);
            boolean isReserved = reservedHours.contains(time);
            slots.add(new TimeSlot(court, date, time, !isReserved));
        }
        return slots;
    }

    public List<TimeSlot> getSchedule(String courtIdStr, String dateStr) {
        if (courtIdStr == null || courtIdStr.isBlank() || dateStr == null || dateStr.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Long courtId = Long.valueOf(courtIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            return getSchedule(courtId, date);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public TimeSlot reserve(Long courtId, LocalDate date, LocalTime hour) {
        Court court = courtService.findById(courtId);
        TimeSlot slot = new TimeSlot(court, date, hour, false);
        return timeSlotService.save(slot);
    }

    public TimeSlot reserve(String courtIdStr, String dateStr, String hourStr) {
        try {
            Long courtId = Long.valueOf(courtIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime hour = LocalTime.parse(hourStr);
            return reserve(courtId, date, hour);
        } catch (Exception e) {
            return null;
        }
    }
}
