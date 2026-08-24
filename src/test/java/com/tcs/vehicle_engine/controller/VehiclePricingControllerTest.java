package com.tcs.vehicle_engine.controller;

import com.tcs.vehicle_engine.repository.VehicleRepository;
import com.tcs.vehicle_engine.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiclePricingController.class)
@Import({PricingService.class, VehicleRepository.class})
class VehiclePricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPricingSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1001/pricing")
                        .param("downPayment", "5000")
                        .param("apr", "4.5")
                        .param("loanTerm", "36"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(1001))
                .andExpect(jsonPath("$.monthlyPayment").exists());
    }

    @Test
    void testGetPricingVehicleNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/9999/pricing")
                        .param("downPayment", "5000")
                        .param("apr", "4.5")
                        .param("loanTerm", "36"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testGetPricingBadRequestInvalidDownPayment() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1001/pricing")
                        .param("downPayment", "35000")
                        .param("apr", "4.5")
                        .param("loanTerm", "36"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
