package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.model.Vehicle;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class PricingService {

    private final VehicleRepository vehicleRepository;

    private static final Set<Integer> VALID_TERMS = new HashSet<>(Arrays.asList(12, 24, 36, 48, 60, 72, 84));

    public PricingService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public PricingResponse calculatePricing(Long vehicleId, BigDecimal downPayment, BigDecimal apr, Integer loanTerm) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than zero");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Down payment must be greater than or equal to zero");
        }
        if (apr == null || apr.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("APR must be greater than or equal to zero");
        }
        if (loanTerm == null || !VALID_TERMS.contains(loanTerm)) {
            throw new IllegalArgumentException("Loan term must be one of: 12, 24, 36, 48, 60, 72, or 84 months");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Vehicle not found with ID: " + vehicleId));

        BigDecimal vehiclePrice = vehicle.getVehiclePrice();

        if (downPayment.compareTo(vehiclePrice) >= 0) {
            throw new IllegalArgumentException("Down payment must be less than vehicle price");
        }

        BigDecimal principal = vehiclePrice.subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(new BigDecimal(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal monthlyInterestRate = apr.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);

            BigDecimal onePlusRDivided = BigDecimal.ONE.add(monthlyInterestRate);
            BigDecimal powValue = onePlusRDivided.pow(loanTerm, MathContext.DECIMAL128);

            BigDecimal numerator = monthlyInterestRate.multiply(powValue);
            BigDecimal denominator = powValue.subtract(BigDecimal.ONE);

            BigDecimal factor = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
            monthlyPayment = principal.multiply(factor);
        }

        BigDecimal roundedMonthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);

        return new PricingResponse(vehicleId, vehiclePrice, downPayment, apr, loanTerm, roundedMonthlyPayment);
    }
}
