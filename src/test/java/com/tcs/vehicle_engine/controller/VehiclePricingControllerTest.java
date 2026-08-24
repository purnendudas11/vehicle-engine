package com.tcs.vehicle_engine.controller;

import com.tcs.vehicle_engine.repository.VehicleRepository;
import com.tcs.vehicle_engine.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehiclePricingController.class)
@Import({PricingService.class, VehicleRepository.class})
class VehiclePricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPricingSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1001/pricing")
                        .param("downPayment", "5000.00")
                        .param("apr", "5.0")
                        .param("loanTerm", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(1001))
                .andExpect(jsonPath("$.monthlyPayment").value("377.42"));
    }

    @Test
    void testGetPricingVehicleNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/9999/pricing")
                        .param("downPayment", "0.00")
                        .param("apr", "5.0")
                        .param("loanTerm", "60"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void testGetPricingBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1001/pricing")
                        .param("downPayment", "-100.00")
                        .param("apr", "5.0")
                        .param("loanTerm", "60"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
