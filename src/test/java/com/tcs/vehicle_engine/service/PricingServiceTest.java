package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.exception.VehicleNotFoundException;
import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTest {

    private PricingService pricingService;
    private VehicleRepository vehicleRepository;

    @BeforeEach
    public void setUp() {
        vehicleRepository = new VehicleRepository();
        vehicleRepository.init();
        pricingService = new PricingService(vehicleRepository);
    }

    @Test
    public void testSuccessfulCalculation() {
        PricingResponse response = pricingService.calculatePricing(1L, new BigDecimal("5000"), new BigDecimal("5.0"), 36);
        assertNotNull(response);
        assertEquals(1L, response.getVehicleId());
        assertEquals(0, new BigDecimal("25000.00").compareTo(response.getVehiclePrice()));
        assertEquals(0, new BigDecimal("5000").compareTo(response.getDownPayment()));
        assertNotNull(response.getMonthlyPayment());
    }

    @Test
    public void testZeroAprCalculation() {
        PricingResponse response = pricingService.calculatePricing(1L, new BigDecimal("5000"), BigDecimal.ZERO, 36);
        assertNotNull(response);
        assertEquals(0, new BigDecimal("555.56").compareTo(response.getMonthlyPayment()));
    }

    @Test
    public void testInvalidVehicleId() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(0L, BigDecimal.ZERO, BigDecimal.TEN, 36);
        });
    }

    @Test
    public void testVehicleNotFound() {
        assertThrows(VehicleNotFoundException.class, () -> {
            pricingService.calculatePricing(999L, BigDecimal.ZERO, BigDecimal.TEN, 36);
        });
    }

    @Test
    public void testInvalidDownPayment() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(1L, new BigDecimal("30000"), BigDecimal.TEN, 36);
        });
    }

    @Test
    public void testInvalidLoanTerm() {
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculatePricing(1L, BigDecimal.ZERO, BigDecimal.TEN, 15);
        });
    }
}
