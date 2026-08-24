package com.tcs.vehicle_engine.controller;

import com.tcs.vehicle_engine.model.VehiclePricingResponse;
import com.tcs.vehicle_engine.service.VehiclePricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehiclePricingController {

    private final VehiclePricingService vehiclePricingService;

    public VehiclePricingController(VehiclePricingService vehiclePricingService) {
        this.vehiclePricingService = vehiclePricingService;
    }

    @GetMapping("/{vehicleId}/pricing")
    public ResponseEntity<VehiclePricingResponse> getVehiclePricing(
            @PathVariable Long vehicleId,
            @RequestParam BigDecimal downPayment,
            @RequestParam BigDecimal apr,
            @RequestParam Integer loanTerm) {
        VehiclePricingResponse response = vehiclePricingService.calculatePricing(vehicleId, downPayment, apr, loanTerm);
        return ResponseEntity.ok(response);
    }
}
