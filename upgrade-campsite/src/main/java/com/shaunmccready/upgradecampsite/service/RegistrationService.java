package com.shaunmccready.upgradecampsite.service;

import com.shaunmccready.upgradecampsite.domain.AvailableDays;
import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.domain.Registration;
import com.shaunmccready.upgradecampsite.domain.RegistrationDays;
import com.shaunmccready.upgradecampsite.exception.ReservationException;
import com.shaunmccready.upgradecampsite.repository.RegistrationDao;
import com.shaunmccready.upgradecampsite.utils.RegistrationConstraints;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class RegistrationService {

    private RegistrationDao registrationDao;

    private CamperService camperService;


    public RegistrationService(RegistrationDao registrationDao, CamperService camperService) {
        this.registrationDao = registrationDao;
        this.camperService = camperService;
    }

    public AvailableDays getAvailability(final String availabilityFrom, final String availabilityTo) {
        RegistrationDays registrationDays = RegistrationConstraints.processForValidDates(availabilityFrom, availabilityTo, true);

        Set<LocalDate> reservedDays = registrationDao.findAllReservedDays(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());

        AvailableDays availableDays = new AvailableDays();
        long daysBetween = ChronoUnit.DAYS.between(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());

        Stream.iterate(registrationDays.getArrivalDate(), d -> d.plusDays(1))
                .limit(daysBetween + 1)
                .forEach(day -> {
                    if (!reservedDays.contains(day)) {
                        availableDays.getAvailableDays().add(day);
                    }
                });

        System.out.println(availableDays);
        return availableDays;
    }

    @Transactional
    public List<Registration> reserveCamp(final String fromDate, final String toDate, final Camper camper) {
        RegistrationDays registrationDays = RegistrationConstraints.validateAndProcessForReservation(fromDate, toDate);
        Set<LocalDate> conflictingReservations = registrationDao.findAllReservedDays(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());

        if (!conflictingReservations.isEmpty()) {
            throw new ReservationException("Could not process the reservation. Theres one or more days previously reserved by another client, specifically " + conflictingReservations);
        }

        List<Registration> daysToReserve = createRegistrationsToReserve(camper, registrationDays);
        return registrationDao.saveAll(daysToReserve);
    }

    private List<Registration> createRegistrationsToReserve(final Camper camper, final RegistrationDays registrationDays) {
        Camper camperFromDb = camperService.createOrRetrieveCamper(camper);

        List<Registration> daysToReserve = new ArrayList<>();
        long daysBetween = ChronoUnit.DAYS.between(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());

        Stream.iterate(registrationDays.getArrivalDate(), d -> d.plusDays(1))
                .limit(daysBetween + 1)
                .forEach(day -> {
                    Registration registration = new Registration(camperFromDb, day);
                    daysToReserve.add(registration);
                });

        return daysToReserve;
    }
}
