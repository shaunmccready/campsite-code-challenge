package com.shaunmccready.upgradecampsite.integration;

import com.shaunmccready.upgradecampsite.domain.AvailableDays;
import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.domain.Registration;
import com.shaunmccready.upgradecampsite.repository.RegistrationDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationDao registrationDao;

    @Test
    public void checkAvailabilityTest_returnAllDaysAvailable_SUCCESS() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(2);

        AvailableDays availability = registrationService.getAvailability(fromDate.toString(), toDate.toString());
        Assertions.assertThat(availability.getAvailableDays()).isNotNull();
        Assertions.assertThat(availability.getAvailableDays().size()).isEqualTo(2);
        Assertions.assertThat(availability.getAvailableDays().contains(fromDate)).isTrue();
        Assertions.assertThat(availability.getAvailableDays().contains(toDate)).isTrue();
    }

    @Test
    public void checkAvailabilityTest_returnDefaultOneMonth_SUCCESS() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusMonths(1);

        AvailableDays availability = registrationService.getAvailability(null, null);
        Assertions.assertThat(availability.getAvailableDays()).isNotNull();
        Assertions.assertThat(availability.getAvailableDays().size()).isGreaterThanOrEqualTo(28);
        Assertions.assertThat(availability.getAvailableDays().size()).isLessThanOrEqualTo(32);
        Assertions.assertThat(availability.getAvailableDays().contains(fromDate)).isTrue();
        Assertions.assertThat(availability.getAvailableDays().contains(toDate)).isTrue();
    }

    @Test
    public void reserveCampsiteTest_passingAllRequiredFields_SUCCESS() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(2);

        List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        Assertions.assertThat(registrations).isNotEmpty();
        Assertions.assertThat(registrations.get(0).getBookingId()).isNotBlank();
        Assertions.assertThat(registrations.size()).isEqualTo(2);
        Assertions.assertThat(registrations.get(0).getReservationDate()).isEqualTo(fromDate);
        Assertions.assertThat(registrations.get(1).getReservationDate()).isEqualTo(toDate);
    }

    @Test
    public void reserveCampsiteTest_missingRequiredFields_FAIL() {
        Camper camper = new Camper();
        camper.setEmail("fake@fake.com");

        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(2);

        org.junit.jupiter.api.Assertions.assertThrows(TransactionSystemException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    public void reserveCampsiteTest_reserveMoreThan3Days_FAIL() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(5);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    public void reserveCampsiteTest_reserveMinimumOneDayAdvanceArrival_FAIL() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    public void reserveCampsiteTest_reserveMaximumOneMonthAdvanceArrival_FAIL() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now().plusMonths(1);
        LocalDate toDate = LocalDate.now().plusMonths(1).plusDays(2);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    public void cancelReservationTest_passingAllRequiredFields_SUCCESS() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(2);

        List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        Assertions.assertThat(registrations).isNotEmpty();
        Assertions.assertThat(registrations.get(0).getBookingId()).isNotBlank();
        Assertions.assertThat(registrations.size()).isEqualTo(2);
        Assertions.assertThat(registrations.get(0).getReservationDate()).isEqualTo(fromDate);
        Assertions.assertThat(registrations.get(1).getReservationDate()).isEqualTo(toDate);

        String bookingId = registrations.get(0).getBookingId();
        registrationService.cancelReservation(bookingId);

        List<Registration> findByBookingId = registrationDao.findByBookingId(bookingId);
        Assertions.assertThat(findByBookingId).isEmpty();
    }

    @Test
    public void modifyReservationTest_passingAvaiableDaysAndExistingBookingId_SUCCESS() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate originalFromDate = LocalDate.now().plusDays(1);
        LocalDate originalToDate = LocalDate.now().plusDays(2);

        List<Registration> registrations = registrationService.reserveCampsite(originalFromDate.toString(), originalToDate.toString(), camper);
        Assertions.assertThat(registrations).isNotEmpty();
        Assertions.assertThat(registrations.get(0).getBookingId()).isNotBlank();
        Assertions.assertThat(registrations.size()).isEqualTo(2);
        Assertions.assertThat(registrations.get(0).getReservationDate()).isEqualTo(originalFromDate);
        Assertions.assertThat(registrations.get(1).getReservationDate()).isEqualTo(originalToDate);

        String originalBookingId = registrations.get(0).getBookingId();
        LocalDate newFromDate = LocalDate.now().plusDays(10);
        LocalDate newToDate = LocalDate.now().plusDays(11);

        List<Registration> modifiedRegistrations = registrationService.modifyReservation(originalBookingId, newFromDate.toString(), newToDate.toString());
        Assertions.assertThat(modifiedRegistrations.get(0).getBookingId()).isEqualTo(originalBookingId);
        Assertions.assertThat(modifiedRegistrations.get(0).getReservationDate()).isEqualTo(newFromDate);
        Assertions.assertThat(modifiedRegistrations.get(1).getReservationDate()).isEqualTo(newToDate);


        List<Registration> findByBookingId = registrationDao.findByBookingId(originalBookingId);
        long count = findByBookingId.stream()
                .filter(registration -> {
                    return registration.getReservationDate().equals(originalFromDate) ||
                            registration.getReservationDate().equals(originalToDate);
                })
                .count();

        Assertions.assertThat(count).isEqualTo(0);
    }


}
