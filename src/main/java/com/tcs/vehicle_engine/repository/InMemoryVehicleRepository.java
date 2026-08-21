package com.tcs.vehicle_engine.repository;

import com.tcs.vehicle_engine.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InMemoryVehicleRepository extends JpaRepository<Vehicle, Long> {
}