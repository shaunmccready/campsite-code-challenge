package com.shaunmccready.upgradecampsite.utils;

import com.shaunmccready.upgradecampsite.domain.RegistrationDays;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RegistrationConstraints {

    /**
     * Can Only reserve for a maximum of 3 consecutive days.
     */
    private static final int MAXIMUM_NUMBER_OF_CONSECUTIVE_DAYS = 3;

    public static RegistrationDays processForValidDates(String availabilityFrom, String availabilityTo, boolean allowBlankDates) {
        if (isEitherTheFromOrToDateBlankAndAllowBlankDates(availabilityFrom, availabilityTo, allowBlankDates)) {
            LocalDate fromDate = LocalDate.now();
            LocalDate toDate = LocalDate.now().plusMonths(1);
            return new RegistrationDays(fromDate, toDate);
        }

        RegistrationDays parseDates = parseDates(availabilityFrom, availabilityTo);
        validateChronologicalOrder(parseDates);

        return parseDates;
    }

    private static boolean isEitherTheFromOrToDateBlankAndAllowBlankDates(String availabilityFrom, String availabilityTo, boolean allowBlankDates) {
        return (StringUtils.isBlank(availabilityFrom) || StringUtils.isBlank(availabilityTo)) && allowBlankDates;
    }

    private static RegistrationDays parseDates(String availabilityFrom, String availabilityTo) throws IllegalArgumentException{
        try {
            LocalDate fromDate = LocalDate.parse(availabilityFrom, DateTimeFormatter.ISO_DATE);
            LocalDate toDate = LocalDate.parse(availabilityTo, DateTimeFormatter.ISO_DATE);
            return new RegistrationDays(fromDate, toDate);
        } catch (Exception e) {
            throw new IllegalArgumentException("The date(s) that were passed were not formatted correctly. They need to be in the format 'YYYY-MM-DD");
        }
    }

    private static void validateChronologicalOrder(RegistrationDays parseDates) throws IllegalArgumentException{
        boolean verifyDateOrder = parseDates.getArrivalDate().isBefore(parseDates.getDepartureDate()) ||
                parseDates.getArrivalDate().isEqual(parseDates.getDepartureDate());
        if (!verifyDateOrder) {
            throw new IllegalArgumentException("The 'from' date doesn't come before the 'to' date");
        }
    }

    public static RegistrationDays validateAndProcessForReservation(String fromDate, String toDate) {
        RegistrationDays registrationDays = processForValidDates(fromDate, toDate, false);
        verifyMaximum3DayRegistration(registrationDays);
        verifyArrivalatLeastOneDayAhead(registrationDays);
        verifyArrivalMaximum1MonthInAdvance(registrationDays);

        return registrationDays;
    }

    private static void verifyMaximum3DayRegistration(RegistrationDays registrationDays) throws IllegalArgumentException{
        long numberOfReservedDays = ChronoUnit.DAYS.between(registrationDays.getArrivalDate(), registrationDays.getDepartureDate());

        if (numberOfReservedDays > MAXIMUM_NUMBER_OF_CONSECUTIVE_DAYS) {
            throw new IllegalArgumentException("You cannot reserve the campsite for more than 3 days in a row");
        }
    }

    private static void verifyArrivalatLeastOneDayAhead(RegistrationDays registrationDays) throws IllegalArgumentException{
        LocalDate today = LocalDate.now();
        if (today.isEqual(registrationDays.getArrivalDate()) || today.isAfter(registrationDays.getArrivalDate())) {
            throw new IllegalArgumentException("You must reserve at least one day in advance. Earliest day to try must be " + today.plusDays(1));
        }
    }

    public static void verifyArrivalMaximum1MonthInAdvance (RegistrationDays registrationDays) throws IllegalArgumentException{
        LocalDate oneMonthInAdvance = LocalDate.now().plusMonths(1);
        if (!oneMonthInAdvance.isAfter(registrationDays.getArrivalDate())) {
            throw new IllegalArgumentException("Arrival date is too far ahead. You can only reserve for a maximum of one month in advance, which would be " + oneMonthInAdvance);
        }
    }

}