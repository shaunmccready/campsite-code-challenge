package com.shaunmccready.upgradecampsite.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "camper_id", nullable = false)
    private Camper camper;

    private LocalDate reservationDate;

    private LocalDateTime created;

    private LocalDateTime modified;

    public Registration() {
    }

    public Registration(Camper camper, LocalDate reservationDate) {
        this.camper = camper;
        this.reservationDate = reservationDate;
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
    }

    public String getBookingId() {
        return bookingId;
    }

    public Registration setBookingId(String bookingId) {
        this.bookingId = bookingId;
        return this;
    }

    public Camper getCamper() {
        return camper;
    }

    public Registration setCamper(Camper camper) {
        this.camper = camper;
        return this;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public Registration setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
        return this;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public Registration setCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public Registration setModified(LocalDateTime modified) {
        this.modified = modified;
        return this;
    }
}
