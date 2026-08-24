package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.exception.BadRequestException;
import com.tcs.vehicle_engine.exception.VehicleNotFoundException;
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
    void testCalculatePricingSuccessNonZeroApr() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("5000.00"), new BigDecimal("5.0"), 60);
        assertNotNull(response);
        assertEquals(1001L, response.getVehicleId());
        assertEquals(new BigDecimal("25000.00"), response.getVehiclePrice());
        assertEquals(new BigDecimal("377.42"), response.getMonthlyPayment());
    }

    @Test
    void testCalculatePricingSuccessZeroApr() {
        PricingResponse response = pricingService.calculatePricing(1001L, new BigDecimal("5000.00"), BigDecimal.ZERO, 60);
        assertNotNull(response);
        assertEquals(new BigDecimal("333.33"), response.getMonthlyPayment());
    }

    @Test
    void testVehicleNotFound() {
        assertThrows(VehicleNotFoundException.class, () -> {
            pricingService.calculatePricing(9999L, BigDecimal.ZERO, new BigDecimal("5.0"), 60);
        });
    }

    @Test
    void testInvalidVehicleId() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(0L, BigDecimal.ZERO, new BigDecimal("5.0"), 60);
        });
    }

    @Test
    void testInvalidDownPaymentNegative() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, new BigDecimal("-1.00"), new BigDecimal("5.0"), 60);
        });
    }

    @Test
    void testInvalidDownPaymentTooHigh() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, new BigDecimal("25000.00"), new BigDecimal("5.0"), 60);
        });
    }

    @Test
    void testInvalidApr() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, BigDecimal.ZERO, new BigDecimal("-0.1"), 60);
        });
    }

    @Test
    void testInvalidLoanTerm() {
        assertThrows(BadRequestException.class, () -> {
            pricingService.calculatePricing(1001L, BigDecimal.ZERO, new BigDecimal("5.0"), 50);
        });
    }
}
