package com.shaunmccready.upgradecampsite.integration;

import com.shaunmccready.upgradecampsite.domain.AvailableDays;
import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.domain.Registration;
import com.shaunmccready.upgradecampsite.repository.RegistrationDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Rollback
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
    @Rollback
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
    @Rollback
    public void reserveCampsiteTest_reserveMoreThan3Days_FAIL() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now().plusDays(5);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    @Rollback
    public void reserveCampsiteTest_reserveMinimumOneDayAdvanceArrival_FAIL() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(2);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    @Rollback
    public void reserveCampsiteTest_reserveMaximumOneMonthAdvanceArrival_FAIL() {
        Camper camper = Camper.of("fake@fake.com", "John Smith");
        LocalDate fromDate = LocalDate.now().plusMonths(1);
        LocalDate toDate = LocalDate.now().plusMonths(1).plusDays(2);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            List<Registration> registrations = registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        });
    }

    @Test
    @Rollback
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
    @Rollback
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
                .filter(registration ->
                        registration.getReservationDate().equals(originalFromDate) ||
                        registration.getReservationDate().equals(originalToDate))
                .count();

        Assertions.assertThat(count).isEqualTo(0);
    }

    @Disabled
    @Test
    @Rollback
    public void concurrentRequestsToReserveTest() {

        Callable callable = () -> {
            String random = RandomStringUtils.random(5, true, true);
            String email = random + "@" + random + ".com";

            Camper camper = Camper.of(email, "John Smith");

            LocalDate fromDate = LocalDate.now().plusDays(1);
            LocalDate toDate = LocalDate.now().plusDays(2);

            return registrationService.reserveCampsite(fromDate.toString(), toDate.toString(), camper);
        };

        int totalthreads = 10;
        int totalRequests = 1000;
        int waitTimeForThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(totalthreads);

        List<Future<List<Registration>>> registrationRequests = Collections.synchronizedList(new ArrayList<>(totalRequests));
        for (int i = 0; i < totalRequests; i++) {
            registrationRequests.add(executorService.submit(callable));
        }

        List<List<Registration>> resultOfConcurrentReserves = Collections.synchronizedList(new ArrayList<>(totalRequests));

        AtomicInteger failedRequests = new AtomicInteger(0);
        for (Future<List<Registration>> registrationFuture : registrationRequests) {
            while (!registrationFuture.isDone()) {
                try {
                    Thread.sleep(waitTimeForThread);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            try {
                resultOfConcurrentReserves.add(registrationFuture.get());
            } catch (InterruptedException e) {
                failedRequests.incrementAndGet();
            } catch (ExecutionException e) {
                failedRequests.incrementAndGet();
            }
        }

        executorService.shutdown();

        Assertions.assertThat(resultOfConcurrentReserves).isNotEmpty();
        Assertions.assertThat(resultOfConcurrentReserves.get(0)).isNotEmpty();
        Assertions.assertThat(resultOfConcurrentReserves.get(0).size()).isEqualTo(2);

        //Only one reservation should be successful. The rest should all fail because of data constraints within the DB
        Assertions.assertThat(failedRequests.get()).isEqualTo(totalRequests - 1);
    }

}
