package com.tcs.vehicle_engine.service;

import com.tcs.vehicle_engine.dto.PricingResponseDto;
import com.tcs.vehicle_engine.exception.BadRequestException;
import com.tcs.vehicle_engine.exception.ResourceNotFoundException;
import com.tcs.vehicle_engine.model.Vehicle;
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
    private static final Set<Integer> VALID_TERMS = new HashSet<>(Arrays.asList(12, 24, 36, 48, 60, 72, 84));

    private final VehicleRepository vehicleRepository;

    public VehiclePricingService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public PricingResponseDto calculatePricing(Long vehicleId, BigDecimal downPayment, BigDecimal apr, Integer loanTerm) {
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
            throw new BadRequestException("Loan term must be one of: 12, 24, 36, 48, 60, 72, or 84 months.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + vehicleId + " not found."));

        if (downPayment.compareTo(vehicle.getVehiclePrice()) >= 0) {
            throw new BadRequestException("Down payment must be less than vehicle price.");
        }

        BigDecimal p = vehicle.getVehiclePrice().subtract(downPayment);
        BigDecimal monthlyPayment;

        if (apr.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = p.divide(new BigDecimal(loanTerm), 10, RoundingMode.HALF_UP);
        } else {
            BigDecimal i = apr.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);

            BigDecimal onePlusI = BigDecimal.ONE.add(i);
            BigDecimal onePlusIPowN = onePlusI.pow(loanTerm, MathContext.DECIMAL128);

            BigDecimal numerator = p.multiply(i).multiply(onePlusIPowN);
            BigDecimal denominator = onePlusIPowN.subtract(BigDecimal.ONE);

            monthlyPayment = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        }

        BigDecimal roundedMonthlyPayment = monthlyPayment.setScale(2, RoundingMode.HALF_UP);

        return new PricingResponseDto(
                vehicle.getVehicleId(),
                vehicle.getVehiclePrice(),
                downPayment,
                apr,
                loanTerm,
                roundedMonthlyPayment
        );
    }
}
