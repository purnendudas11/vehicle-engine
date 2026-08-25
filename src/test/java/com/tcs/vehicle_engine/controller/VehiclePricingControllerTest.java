package com.tcs.vehicle_engine.controller;

import com.tcs.vehicle_engine.repository.VehicleRepository;
import com.tcs.vehicle_engine.service.VehiclePricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiclePricingController.class)
@Import({VehiclePricingService.class, VehicleRepository.class})
class VehiclePricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetVehiclePricingSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1001/pricing")
                        .param("downPayment", "5000.00")
                        .param("apr", "4.5")
                        .param("loanTerm", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(1001))
                .andExpect(jsonPath("$.vehiclePrice").value(30000.00))
                .andExpect(jsonPath("$.downPayment").value(5000.00))
                .andExpect(jsonPath("$.apr").value(4.5))
                .andExpect(jsonPath("$.loanTerm").value(60))
                .andExpect(jsonPath("$.monthlyPayment").exists());
    }

    @Test
    void testGetVehiclePricingNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/9999/pricing")
                        .param("downPayment", "1000.00")
                        .param("apr", "4.5")
                        .param("loanTerm", "60"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGetVehiclePricingBadRequestInvalidTerm() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1001/pricing")
                        .param("downPayment", "1000.00")
                        .param("apr", "4.5")
                        .param("loanTerm", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }
}
