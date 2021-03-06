package com.shaunmccready.upgradecampsite.integration;

import com.shaunmccready.upgradecampsite.domain.AvailableDays;
import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.domain.Registration;
import com.shaunmccready.upgradecampsite.domain.RegistrationDays;
import com.shaunmccready.upgradecampsite.exception.ReservationException;
import com.shaunmccready.upgradecampsite.repository.RegistrationDao;
import com.shaunmccready.upgradecampsite.utils.RegistrationConstraints;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
public class RegistrationService {

    private RegistrationDao registrationDao;

    private CamperService camperService;


    public RegistrationService(RegistrationDao registrationDao, CamperService camperService) {
        this.registrationDao = registrationDao;
        this.camperService = camperService;
    }

    public AvailableDays getAvailability(final String availabilityFrom, final String availabilityTo) throws IllegalArgumentException {
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

        Collections.sort(availableDays.getAvailableDays());
        return availableDays;
    }

    @Transactional
    public List<Registration> reserveCampsite(final String fromDate, final String toDate, final Camper camper) throws ReservationException {
        RegistrationDays registrationDays = RegistrationConstraints.validateAndProcessForReservation(fromDate, toDate);
        verifyNoConflictingReservations(registrationDays);

        Camper camperFromDb = camperService.createOrRetrieveCamper(camper);
        verifyNoChainingReservations(registrationDays.getArrivalDate(), camperFromDb.getId());

        return createRegistrationsToReserve(camperFromDb, registrationDays);
    }

    private void verifyNoConflictingReservations(RegistrationDays registrationDays) {
        Set<LocalDate> conflictingReservations = registrationDao.findAllReservedDays(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());

        if (!conflictingReservations.isEmpty()) {
            throw new ReservationException("Could not process the reservation. Theres one or more days previously reserved, specifically " + conflictingReservations);
        }
    }

    @Transactional
    protected List<Registration> createRegistrationsToReserve(final Camper camper, final RegistrationDays registrationDays) {
        return createRegistrationsToReserve(camper, registrationDays, null);
    }

    @Transactional
    protected List<Registration> createRegistrationsToReserve(final Camper camper, final RegistrationDays registrationDays, final String bookingId) {
        List<Registration> daysToReserve = new ArrayList<>();
        long daysBetween = ChronoUnit.DAYS.between(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());
        String bookingIdOfNewRegistration = StringUtils.isNotBlank(bookingId) ? bookingId : UUID.randomUUID().toString();

        Stream.iterate(registrationDays.getArrivalDate(), d -> d.plusDays(1))
                .limit(daysBetween + 1)
                .forEach(day -> {
                    Registration registration = new Registration(camper, day);
                    registration.setBookingId(bookingIdOfNewRegistration);
                    daysToReserve.add(registration);
                });

        return registrationDao.saveAll(daysToReserve);
    }

    private void verifyNoChainingReservations(final LocalDate fromDate, final String camperId) {
        Optional<Registration> chainingReservation = registrationDao.verifyNoChainingReservations(fromDate.minusDays(1), camperId);
        if (chainingReservation.isPresent()) {
            throw new ReservationException("You are not allowed back to back bookings. It prevents others from the campsite. Earliest arrival date to try " + fromDate.plusDays(1));
        }
    }

    private void verifyNoChainingReservationsWithDifferentBooking(final LocalDate fromDate, final String camperId, final String bookingId) {
        Optional<Registration> chainingReservation = registrationDao.verifyNoChainingReservations(fromDate.minusDays(1), camperId);
        if (chainingReservation.isPresent() && !chainingReservation.get().getBookingId().equalsIgnoreCase(bookingId)) {
            throw new ReservationException("You are not allowed back to back bookings. It prevents others from the campsite. Earliest arrival date to try " + fromDate.plusDays(1));
        }
    }

    @Transactional
    public List<Registration> cancelReservation(final String bookingId) {
        if (StringUtils.isBlank(bookingId)) {
            throw new ReservationException("You must provide a Booking Id");
        }

        List<Registration> registrationsByBookingId = registrationDao.findByBookingId(bookingId);
        if (registrationsByBookingId.isEmpty()) {
            throw new ReservationException("There are no reservations listed with the Booking Id:" + bookingId);
        }

        registrationDao.deleteByBookingId(bookingId);
        return registrationsByBookingId;
    }

    @Transactional
    public List<Registration> modifyReservation(final String bookingId, final String fromDate, final String toDate) {
        List<Registration> registrationByBookingId = registrationDao.findByBookingId(bookingId);

        if (registrationByBookingId.isEmpty()) {
            throw new ReservationException("No reservation exists with this Booking Id:" + bookingId);
        }

        RegistrationDays newRegistrationDays = RegistrationConstraints.validateAndProcessForReservation(fromDate, toDate);
        verifyNoConflictingReservations(newRegistrationDays);

        Camper camper = registrationByBookingId.get(0).getCamper();
        verifyNoChainingReservationsWithDifferentBooking(newRegistrationDays.getArrivalDate(), camper.getId(), bookingId);

        registrationDao.deleteByBookingId(bookingId);

        return createRegistrationsToReserve(camper, newRegistrationDays, bookingId);
    }

}
