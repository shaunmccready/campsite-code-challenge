package com.shaunmccready.upgradecampsite.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Helped class to let the client know which days have not been reserved yet
 */
public class AvailableDays {

    private List<LocalDate> availableDays = new ArrayList<>();

    public List<LocalDate> getAvailableDays() {
        return availableDays;
    }

    public AvailableDays setAvailableDays(List<LocalDate> availableDays) {
        this.availableDays = availableDays;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "availableDays=" + availableDays +
                '}';
    }
}
