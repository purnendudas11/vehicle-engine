package com.tcs.vehicle_engine.repository;

import com.tcs.vehicle_engine.model.Vehicle;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class VehicleRepository {
    private final Map<Long, Vehicle> storage = new HashMap<>();

    @PostConstruct
    public void init() {
        storage.put(1L, new Vehicle(1L, new BigDecimal("25000.00")));
        storage.put(2L, new Vehicle(2L, new BigDecimal("32000.00")));
        storage.put(3L, new Vehicle(3L, new BigDecimal("45000.00")));
        storage.put(4L, new Vehicle(4L, new BigDecimal("18000.00")));
        storage.put(5L, new Vehicle(5L, new BigDecimal("55000.00")));
    }

    public Optional<Vehicle> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
}
