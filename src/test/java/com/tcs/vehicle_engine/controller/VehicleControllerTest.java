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

@WebMvcTest(VehicleController.class)
@Import({PricingService.class, VehicleRepository.class})
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetPricingSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1/pricing")
                        .param("downPayment", "2000")
                        .param("apr", "4.5")
                        .param("loanTerm", "48"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(1))
                .andExpect(jsonPath("$.monthlyPayment").exists());
    }

    @Test
    public void testGetPricingNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/999/pricing")
                        .param("downPayment", "2000")
                        .param("apr", "4.5")
                        .param("loanTerm", "48"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    public void testGetPricingBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/1/pricing")
                        .param("downPayment", "30000")
                        .param("apr", "4.5")
                        .param("loanTerm", "48"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
