package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.init();
        pricingService = new PricingService(vehicleRepository);
    }

    @Test
    void testCalculatePricingZeroApr() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("5000"), BigDecimal.ZERO, 60);
        assertNotNull(response);
        assertEquals(1001L, response.getVehicleId());
        assertEquals(new BigDecimal("25000.00"), response.getVehiclePrice());
        assertEquals(new BigDecimal("5000"), response.getDownPayment());
        assertEquals(BigDecimal.ZERO, response.getApr());
        assertEquals(60, response.getLoanTerm());
        assertEquals(new BigDecimal("333.33"), response.getMonthlyPayment());
    }

    @Test
    void testCalculatePricingNonZeroApr() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("5000"), new BigDecimal("5.0"), 60);
        assertNotNull(response);
        assertEquals(new BigDecimal("377.42"), response.getMonthlyPayment());
    }

    @Test
    void testVehicleNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            pricingService.calculatePricing(9999L, BigDecimal.ZERO, new BigDecimal("5.0"), 60);
        });
    }

    @Test
    void testInvalidVehicleId() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(0L, BigDecimal.ZERO, new BigDecimal("5.0"), 60);
        });
    }

    @Test
    void testInvalidLoanTerm() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(1001L, BigDecimal.ZERO, new BigDecimal("5.0"), 50);
        });
    }

    @Test
    void testDownPaymentGreaterThanPrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(1001L, new BigDecimal("30000"), new BigDecimal("5.0"), 60);
        });
    }
}
