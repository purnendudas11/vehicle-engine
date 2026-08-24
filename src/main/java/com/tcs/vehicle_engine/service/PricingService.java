package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.exception.VehicleNotFoundException;
import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.model.Vehicle;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Service
public class PricingService {

    private final VehicleRepository vehicleRepository;
    private static final List<Integer> VALID_TERMS = Arrays.asList(12, 24, 36, 48, 60, 72, 84);

    public PricingService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public PricingResponse calculatePricing(Long vehicleId, BigDecimal downPayment, BigDecimal apr, Integer loanTerm) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than zero.");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Down payment cannot be negative.");
        }
        if (apr == null || apr.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("APR cannot be negative.");
        }
        if (loanTerm == null || !VALID_TERMS.contains(loanTerm)) {
            throw new IllegalArgumentException("Invalid loan term provided.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with ID " + vehicleId + " not found."));

        if (downPayment.compareTo(vehicle.getVehiclePrice()) >= 0) {
            throw new IllegalArgumentException("Down payment must be less than vehicle price.");
        }

        BigDecimal principal = vehicle.getVehiclePrice().subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(BigDecimal.valueOf(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal monthlyRate = apr.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
            
            BigDecimal onePlusIRaiseToN = BigDecimal.ONE.add(monthlyRate).pow(loanTerm, MathContext.DECIMAL128);
            BigDecimal numerator = monthlyRate.multiply(onePlusIRaiseToN);
            BigDecimal denominator = onePlusIRaiseToN.subtract(BigDecimal.ONE);
            
            monthlyPayment = principal.multiply(numerator).divide(denominator, 10, RoundingMode.HALF_UP);
        }

        monthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);

        return new PricingResponse(
                vehicle.getVehicleId(),
                vehicle.getVehiclePrice(),
                downPayment,
                apr,
                loanTerm,
                monthlyPayment
        );
    }
}
