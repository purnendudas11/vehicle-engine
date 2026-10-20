package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.exception.BadRequestException;
import com.tcs.vehicle_engine.exception.ResourceNotFoundException;
import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        VehicleRepository vehicleRepository = new VehicleRepository();
        pricingService = new PricingService(vehicleRepository);
    }

    @Test
    void testCalculatePricingZeroApr() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("6000"), BigDecimal.ZERO, 36);
        assertNotNull(response);
        assertEquals(new BigDecimal("666.67"), response.getMonthlyPayment());
    }

    @Test
    void testCalculatePricingNonZeroApr() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("5000"), new BigDecimal("5.0"), 60);
        assertNotNull(response);
        assertEquals(new BigDecimal("471.78"), response.getMonthlyPayment());
    }

    @Test
    void testInvalidVehicleId() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(0L, BigDecimal.ZERO, new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testVehicleNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            pricingService.calculatePricing(9999L, BigDecimal.ZERO, new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testNegativeDownPayment() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, new BigDecimal("-100"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testDownPaymentGreaterThanPrice() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, new BigDecimal("35000"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testNegativeApr() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, BigDecimal.ZERO, new BigDecimal("-1.0"), 36);
        });
    }

    @Test
    void testInvalidLoanTerm() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, BigDecimal.ZERO, new BigDecimal("5.0"), 30);
        });
    }
}
