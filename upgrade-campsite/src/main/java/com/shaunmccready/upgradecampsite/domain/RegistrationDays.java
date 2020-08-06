package com.shaunmccready.upgradecampsite.domain;

import java.time.LocalDate;

/**
 * Helper class to work with valid arrival and departure date objects
 */
public class RegistrationDays {

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    public RegistrationDays(LocalDate arrivalDate, LocalDate departureDate) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }
}
