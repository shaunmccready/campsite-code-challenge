package com.shaunmccready.upgradecampsite.controller;

import com.shaunmccready.upgradecampsite.domain.AvailableDays;
import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.domain.Registration;
import com.shaunmccready.upgradecampsite.integration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * This controller takes care of all the registration related endpoints for the API
 */
@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    /**
     * End-point to check availability
     *
     * @param fromDate starting date of range to check
     * @param toDate   end date of range to check
     * @return A list of all available days
     */
    @GetMapping("/availability")
    public ResponseEntity<AvailableDays> getAvailability(@RequestParam(value = "from", required = false) String fromDate,
                                                         @RequestParam(value = "to", required = false) String toDate) {
        AvailableDays availability = registrationService.getAvailability(fromDate, toDate);
        return ResponseEntity.ok(availability);
    }

    /**
     * End-point to reserve the camp site
     *
     * @param fromDate arrival date
     * @param toDate   departure date
     * @param camper   Camper details
     * @return Registration info of the reservation
     */
    @PostMapping("/reserve")
    public ResponseEntity<List<Registration>> reserveCamp(@RequestParam("from") String fromDate,
                                                          @RequestParam("to") String toDate,
                                                          @Valid @RequestBody Camper camper) {
        List<Registration> reservedDates = registrationService.reserveCampsite(fromDate, toDate, camper);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();

        return ResponseEntity.created(location).body(reservedDates);
    }

    /**
     * End-point to modify an existing reservation with the Booking ID
     *
     * @param bookingId booking id of the reservation
     * @param fromDate  new arrival date
     * @param toDate    new departure date
     * @return Updated registration info of the reservation
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<List<Registration>> modifyReservation(@PathVariable("bookingId") String bookingId,
                                                                @RequestParam("from") String fromDate,
                                                                @RequestParam("to") String toDate) {
        List<Registration> modifiedReservedDates = registrationService.modifyReservation(bookingId, fromDate, toDate);

        return ResponseEntity.ok(modifiedReservedDates);
    }

    /**
     * End-point to cancel a reservation using a Booking ID
     *
     * @param bookingId booking id of the reservation
     * @return List of all the cancelled reservations
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<List<Registration>> cancelReservation(@PathVariable("bookingId") String bookingId) {
        List<Registration> cancelledDates = registrationService.cancelReservation(bookingId);
        return ResponseEntity.accepted().body(cancelledDates);
    }
}
