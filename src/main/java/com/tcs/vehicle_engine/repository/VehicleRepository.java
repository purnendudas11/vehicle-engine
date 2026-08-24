package com.tcs.vehicle_engine.repository;

import com.tcs.vehicle_engine.model.Vehicle;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class VehicleRepository {
    private final Map<Long, Vehicle> vehicleStore = new HashMap<>();

    public VehicleRepository() {
        vehicleStore.put(1001L, new Vehicle(1001L, new BigDecimal("25000.00")));
        vehicleStore.put(1002L, new Vehicle(1002L, new BigDecimal("30000.00")));
        vehicleStore.put(1003L, new Vehicle(1003L, new BigDecimal("35000.00")));
        vehicleStore.put(1004L, new Vehicle(1004L, new BigDecimal("40000.00")));
        vehicleStore.put(1005L, new Vehicle(1005L, new BigDecimal("45000.00")));
    }

    public Optional<Vehicle> findById(Long vehicleId) {
        return Optional.ofNullable(vehicleStore.get(vehicleId));
    }
}
