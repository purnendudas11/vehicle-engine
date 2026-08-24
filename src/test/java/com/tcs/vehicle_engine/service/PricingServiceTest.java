package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private PricingService pricingService;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository = new VehicleRepository();
        vehicleRepository.init();
        pricingService = new PricingService(vehicleRepository);
    }

    @Test
    void testCalculatePricingSuccess() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("5000"), new BigDecimal("5.0"), 36);
        assertNotNull(response);
        assertEquals(1001L, response.getVehicleId());
        assertEquals(new BigDecimal("30000.00"), response.getVehiclePrice());
        assertEquals(new BigDecimal("5000"), response.getDownPayment());
        assertEquals(new BigDecimal("5.0"), response.getApr());
        assertEquals(36, response.getLoanTerm());
        assertNotNull(response.getMonthlyPayment());
    }

    @Test
    void testZeroAprCalculation() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("6000"), BigDecimal.ZERO, 24);
        assertNotNull(response);
        assertEquals(new BigDecimal("1000.00"), response.getMonthlyPayment());
    }

    @Test
    void testVehicleNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(9999L, new BigDecimal("1000"), new BigDecimal("4.0"), 36);
        });
    }

    @Test
    void testInvalidDownPayment() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(1001L, new BigDecimal("35000"), new BigDecimal("4.0"), 36);
        });
    }
}
