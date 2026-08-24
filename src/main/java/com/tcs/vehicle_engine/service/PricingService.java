package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.model.Vehicle;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class PricingService {
    private final VehicleRepository vehicleRepository;

    public PricingService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public PricingResponse calculatePricing(Long vehicleId, BigDecimal downPayment, BigDecimal apr, Integer loanTerm) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));

        BigDecimal vehiclePrice = vehicle.getVehiclePrice();

        if (vehicleId <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be greater than zero.");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Down payment cannot be negative.");
        }
        if (downPayment.compareTo(vehiclePrice) >= 0) {
            throw new IllegalArgumentException("Down payment must be less than vehicle price.");
        }
        if (apr == null || apr.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("APR cannot be negative.");
        }
        if (loanTerm == null || !isValidLoanTerm(loanTerm)) {
            throw new IllegalArgumentException("Invalid loan term supplied.");
        }

        BigDecimal principal = vehiclePrice.subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(new BigDecimal(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal monthlyRate = apr.divide(new BigDecimal("100"), MathContext.DECIMAL128)
                    .divide(new BigDecimal("12"), MathContext.DECIMAL128);
            BigDecimal termCount = new BigDecimal(loanTerm);

            BigDecimal onePlusIRate = BigDecimal.ONE.add(monthlyRate);
            BigDecimal powered = onePlusIRate.pow(loanTerm, MathContext.DECIMAL128);

            BigDecimal numerator = principal.multiply(monthlyRate, MathContext.DECIMAL128).multiply(powered, MathContext.DECIMAL128);
            BigDecimal denominator = powered.subtract(BigDecimal.ONE);

            monthlyPayment = numerator.divide(denominator, MathContext.DECIMAL128);
        }

        monthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);

        return new PricingResponse(vehicleId, vehiclePrice, downPayment, apr, loanTerm, monthlyPayment);
    }

    private boolean isValidLoanTerm(Integer term) {
        return term == 12 || term == 24 || term == 36 || term == 48 || term == 60 || term == 72 || term == 84;
    }
}
