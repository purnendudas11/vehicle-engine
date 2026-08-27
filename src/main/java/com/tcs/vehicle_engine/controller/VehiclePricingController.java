package com.tcs.vehicle_engine.controller;

import com.tcs.vehicle_engine.model.PricingResponse;
import com.tcs.vehicle_engine.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class VehiclePricingController {

    private final PricingService pricingService;

    public VehiclePricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/api/v1/vehicles/{vehicleId}/pricing")
    public ResponseEntity<PricingResponse> getVehiclePricing(
            @PathVariable Long vehicleId,
            @RequestParam BigDecimal downPayment,
            @RequestParam BigDecimal apr,
            @RequestParam Integer loanTerm) {
        PricingResponse response = pricingService.calculatePricing(vehicleId, downPayment, apr, loanTerm);
        return ResponseEntity.ok(response);
    }
}
