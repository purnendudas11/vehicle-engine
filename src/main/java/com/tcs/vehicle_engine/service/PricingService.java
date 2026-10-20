package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.exception.BadRequestException;
import com.tcs.vehicle_engine.exception.ResourceNotFoundException;
import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.model.Vehicle;
import com.tcs.vehicle_engine.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Set;

@Service
public class PricingService {

    private final VehicleRepository vehicleRepository;
    private static final Set<Integer> ALLOWED_TERMS = Set.of(12, 24, 36, 48, 60, 72, 84);

    public PricingService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public PricingResponse calculatePricing(Long vehicleId, BigDecimal downPayment, BigDecimal apr, Integer loanTerm) {
        if (vehicleId == null || vehicleId <= 0) {
            throw new BadRequestException("Vehicle ID must be greater than zero");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Down payment cannot be negative");
        }
        if (apr == null || apr.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("APR cannot be negative");
        }
        if (loanTerm == null || !ALLOWED_TERMS.contains(loanTerm)) {
            throw new BadRequestException("Invalid loan term");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        if (downPayment.compareTo(vehicle.getVehiclePrice()) >= 0) {
            throw new BadRequestException("Down payment must be less than vehicle price");
        }

        BigDecimal vehiclePrice = vehicle.getVehiclePrice();
        BigDecimal principal = vehiclePrice.subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(new BigDecimal(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal monthlyInterestRate = apr.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
            
            BigDecimal onePlusIRiseN = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTerm, MathContext.DECIMAL128);
            BigDecimal numerator = principal.multiply(monthlyInterestRate).multiply(onePlusIRiseN);
            BigDecimal denominator = onePlusIRiseN.subtract(BigDecimal.ONE);
            
            monthlyPayment = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        }

        BigDecimal roundedMonthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);

        return new PricingResponse(vehicleId, vehiclePrice, downPayment, apr, loanTerm, roundedMonthlyPayment);
    }
}
