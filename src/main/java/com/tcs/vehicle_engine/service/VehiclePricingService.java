package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.model.Vehicle;
import com.tcs.vehicle_engine.model.VehiclePricingResponse;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class VehiclePricingService {

    private final VehicleRepository vehicleRepository;
    private static final Set<Integer> VALID_LOAN_TERMS = new HashSet<>(Arrays.asList(12, 24, 36, 48, 60, 72, 84));

    public VehiclePricingService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehiclePricingResponse calculatePricing(Long vehicleId, BigDecimal downPayment, BigDecimal apr, Integer loanTerm) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than zero.");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Down payment must be greater than or equal to zero.");
        }
        if (apr == null || apr.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("APR must be greater than or equal to zero.");
        }
        if (loanTerm == null || !VALID_LOAN_TERMS.contains(loanTerm)) {
            throw new IllegalArgumentException("Loan term must be one of 12, 24, 36, 48, 60, 72, or 84 months.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with ID " + vehicleId + " not found."));

        BigDecimal vehiclePrice = vehicle.getVehiclePrice();

        if (downPayment.compareTo(vehiclePrice) >= 0) {
            throw new IllegalArgumentException("Down payment must be less than vehicle price.");
        }

        BigDecimal principal = vehiclePrice.subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(BigDecimal.valueOf(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal i = apr.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
            BigDecimal onePlusI = BigDecimal.ONE.add(i);
            BigDecimal onePlusIToN = onePlusI.pow(loanTerm, MathContext.DECIMAL128);
            
            BigDecimal numerator = principal.multiply(i.multiply(onePlusIToN, MathContext.DECIMAL128), MathContext.DECIMAL128);
            BigDecimal denominator = onePlusIToN.subtract(BigDecimal.ONE);
            
            monthlyPayment = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        }

        BigDecimal roundedMonthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);

        return new VehiclePricingResponse(
                vehicleId,
                vehiclePrice,
                downPayment,
                apr,
                loanTerm,
                roundedMonthlyPayment
        );
    }
}
