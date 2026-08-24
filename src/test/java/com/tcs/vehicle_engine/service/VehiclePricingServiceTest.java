package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.model.VehiclePricingResponse;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VehiclePricingServiceTest {

    private VehiclePricingService vehiclePricingService;

    @BeforeEach
    void setUp() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehiclePricingService = new VehiclePricingService(vehicleRepository);
    }

    @Test
    void testCalculatePricingSuccess() {
        VehiclePricingResponse response = vehiclePricingService.calculatePricing(1001L, new BigDecimal("5000"), new BigDecimal("5.0"), 36);
        assertNotNull(response);
        assertEquals(1001L, response.getVehicleId());
        assertEquals(0, new BigDecimal("25000.00").compareTo(response.getVehiclePrice()));
        assertEquals(0, new BigDecimal("5000").compareTo(response.getDownPayment()));
        assertEquals(0, new BigDecimal("5.0").compareTo(response.getApr()));
        assertEquals(36, response.getLoanTerm());
        assertNotNull(response.getMonthlyPayment());
    }

    @Test
    void testCalculatePricingZeroApr() {
        VehiclePricingResponse response = vehiclePricingService.calculatePricing(1001L, new BigDecimal("5000"), BigDecimal.ZERO, 36);
        assertNotNull(response);
        assertEquals(0, new BigDecimal("555.56").compareTo(response.getMonthlyPayment()));
    }

    @Test
    void testVehicleNotFound() {
        assertThrows(VehicleNotFoundException.class, () -> {
            vehiclePricingService.calculatePricing(9999L, new BigDecimal("1000"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidVehicleId() {
        assertThrows(IllegalArgumentException.class, () -> {
            vehiclePricingService.calculatePricing(0L, new BigDecimal("1000"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidDownPaymentNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("-100"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidDownPaymentTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("30000"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidLoanTerm() {
        assertThrows(IllegalArgumentException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("1000"), new BigDecimal("5.0"), 30);
        });
    }
}
