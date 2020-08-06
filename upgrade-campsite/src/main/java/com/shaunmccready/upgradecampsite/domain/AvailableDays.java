package com.shaunmccready.upgradecampsite.domain;

import java.time.LocalDate;
import java.util.Set;

/**
 * Helped class to let the client know which days have not been reserved yet
 */
public class AvailableDays {

    private Set<LocalDate> availableDays;

    public Set<LocalDate> getAvailableDays() {
        return availableDays;
    }

    public AvailableDays setAvailableDays(Set<LocalDate> availableDays) {
        this.availableDays = availableDays;
        return this;
    }
}
