package com.shaunmccready.upgradecampsite.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "camper")
public class Camper implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private LocalDateTime created;

    @OneToMany(mappedBy = "camper")
    private Set<Registration> registrations;


    public String getId() {
        return id;
    }

    public Camper setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Camper setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Camper setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public Camper setCreated(LocalDateTime created) {
        this.created = created;
        return this;
    }

    public Set<Registration> getRegistrations() {
        return registrations;
    }

    public Camper setRegistrations(Set<Registration> registrations) {
        this.registrations = registrations;
        return this;
    }
}
