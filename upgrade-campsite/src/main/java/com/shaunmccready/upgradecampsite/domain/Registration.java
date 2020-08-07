package com.shaunmccready.upgradecampsite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "registration_id_seq", sequenceName = "public.registration_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "registration_id_seq")
    private Integer id;

    @NotBlank
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "camper_id", nullable = false)
    @JsonIgnoreProperties("registrations")
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

    public Integer getId() {
        return id;
    }

    public Registration setId(Integer id) {
        this.id = id;
        return this;
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
