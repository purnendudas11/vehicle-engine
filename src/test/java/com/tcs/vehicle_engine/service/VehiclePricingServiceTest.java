package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.dto.PricingResponseDto;
import com.tcs.vehicle_engine.exception.BadRequestException;
import com.tcs.vehicle_engine.exception.ResourceNotFoundException;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VehiclePricingServiceTest {

    private VehicleRepository vehicleRepository;
    private VehiclePricingService vehiclePricingService;

    @BeforeEach
    void setUp() {
        vehicleRepository = new VehicleRepository();
        vehiclePricingService = new VehiclePricingService(vehicleRepository);
    }

    @Test
    void testCalculatePricingSuccess() {
        PricingResponseDto dto = vehiclePricingService.calculatePricing(1001L, new BigDecimal("5000.00"), new BigDecimal("5.0"), 60);
        assertNotNull(dto);
        assertEquals(1001L, dto.getVehicleId());
        assertEquals(new BigDecimal("30000.00"), dto.getVehiclePrice());
        assertEquals(new BigDecimal("5000.00"), dto.getDownPayment());
        assertEquals(new BigDecimal("5.0"), dto.getApr());
        assertEquals(60, dto.getLoanTerm());
        assertNotNull(dto.getMonthlyPayment());
    }

    @Test
    void testCalculatePricingZeroApr() {
        PricingResponseDto dto = vehiclePricingService.calculatePricing(1001L, new BigDecimal("6000.00"), BigDecimal.ZERO, 36);
        assertNotNull(dto);
        assertEquals(new BigDecimal("666.67"), dto.getMonthlyPayment());
    }

    @Test
    void testVehicleNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            vehiclePricingService.calculatePricing(9999L, new BigDecimal("1000.00"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidVehicleId() {
        assertThrows(BadRequestException.class, () -> {
            vehiclePricingService.calculatePricing(0L, new BigDecimal("1000.00"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidDownPaymentNegative() {
        assertThrows(BadRequestException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("-100.00"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidDownPaymentExceedsPrice() {
        assertThrows(BadRequestException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("35000.00"), new BigDecimal("5.0"), 36);
        });
    }

    @Test
    void testInvalidApr() {
        assertThrows(BadRequestException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("1000.00"), new BigDecimal("-1.0"), 36);
        });
    }

    @Test
    void testInvalidLoanTerm() {
        assertThrows(BadRequestException.class, () -> {
            vehiclePricingService.calculatePricing(1001L, new BigDecimal("1000.00"), new BigDecimal("5.0"), 30);
        });
    }
}
