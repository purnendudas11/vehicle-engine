package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.exception.BadRequestException;
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
            throw new BadRequestException("Vehicle ID must be greater than zero.");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Down payment must be greater than or equal to zero.");
        }
        if (apr == null || apr.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("APR must be greater than or equal to zero.");
        }
        if (loanTerm == null || !VALID_TERMS.contains(loanTerm)) {
            throw new BadRequestException("Loan term must be one of 12, 24, 36, 48, 60, 72, or 84 months.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with ID " + vehicleId + " not found."));

        if (downPayment.compareTo(vehicle.getVehiclePrice()) >= 0) {
            throw new BadRequestException("Down payment must be less than vehicle price.");
        }

        BigDecimal p = vehicle.getVehiclePrice().subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = p.divide(new BigDecimal(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal i = apr.divide(new BigDecimal("100"), MathContext.DECIMAL128)
                    .divide(new BigDecimal("12"), MathContext.DECIMAL128);
            BigDecimal onePlusI = BigDecimal.ONE.add(i);
            BigDecimal onePlusIToN = onePlusI.pow(loanTerm, MathContext.DECIMAL128);

            BigDecimal numerator = p.multiply(i.multiply(onePlusIToN, MathContext.DECIMAL128), MathContext.DECIMAL128);
            BigDecimal denominator = onePlusIToN.subtract(BigDecimal.ONE);

            monthlyPayment = numerator.divide(denominator, MathContext.DECIMAL128);
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
