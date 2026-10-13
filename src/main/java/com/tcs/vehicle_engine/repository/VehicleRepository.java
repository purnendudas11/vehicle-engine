package com.tcs.vehicle_engine.repository;

import com.tcs.vehicle_engine.model.Vehicle;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class VehicleRepository {
    private final Map<Long, Vehicle> vehicleMap = new HashMap<>();

    public VehicleRepository() {
        vehicleMap.put(1001L, new Vehicle(1001L, new BigDecimal("30000")));
        vehicleMap.put(1002L, new Vehicle(1002L, new BigDecimal("35000")));
        vehicleMap.put(1003L, new Vehicle(1003L, new BigDecimal("40000")));
        vehicleMap.put(1004L, new Vehicle(1004L, new BigDecimal("45000")));
        vehicleMap.put(1005L, new Vehicle(1005L, new BigDecimal("50000")));
    }

    public Optional<Vehicle> findById(Long vehicleId) {
        return Optional.ofNullable(vehicleMap.get(vehicleId));
    }
}
