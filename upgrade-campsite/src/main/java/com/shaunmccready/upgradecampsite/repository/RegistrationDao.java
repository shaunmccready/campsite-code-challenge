package com.shaunmccready.upgradecampsite.repository;

import com.shaunmccready.upgradecampsite.domain.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RegistrationDao extends JpaRepository<Registration, Integer> {

    @Query(value = "SELECT r.reservationDate from Registration r where r.reservationDate >= :fromDate and r.reservationDate <= :toDate")
    Set<LocalDate> findAllReservedDays(@Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate);

    @Query(value = "SELECT r from Registration r where r.reservationDate = :fromDate and r.camper.id = :camperId")
    Optional<Registration> verifyNoChainingReservations(@Param("fromDate") LocalDate fromDate,
                                                        @Param("camperId") String camperId);

    List<Registration> findByBookingId(String bookingId);

    void deleteByBookingId(@Param("bookingId") String bookingId);
}
