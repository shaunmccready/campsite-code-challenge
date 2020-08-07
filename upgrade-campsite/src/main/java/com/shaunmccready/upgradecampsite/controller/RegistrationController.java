package com.shaunmccready.upgradecampsite.controller;

import com.shaunmccready.upgradecampsite.domain.AvailableDays;
import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.domain.Registration;
import com.shaunmccready.upgradecampsite.service.RegistrationService;
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


    @GetMapping("/availability")
    public ResponseEntity<AvailableDays> getAvailability(@RequestParam(value = "from", required = false) String fromDate,
                                                         @RequestParam(value = "to", required = false) String toDate) {
        AvailableDays availability = registrationService.getAvailability(fromDate, toDate);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/reserve")
    public ResponseEntity<List<Registration>> reserveCamp(@RequestParam("from") String fromDate,
                                                          @RequestParam("to") String toDate,
                                                          @Valid @RequestBody Camper camper) {
        List<Registration> reservedDates = registrationService.reserveCamp(fromDate, toDate, camper);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();

        return ResponseEntity.created(location).body(reservedDates);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<List<Registration>> cancelReservation(@PathVariable("bookingId") String bookingId) {
        List<Registration> cancelledDates = registrationService.cancelReservation(bookingId);
        return ResponseEntity.accepted().body(cancelledDates);
    }
}
