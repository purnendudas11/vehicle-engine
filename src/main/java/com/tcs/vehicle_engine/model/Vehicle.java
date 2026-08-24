package com.tcs.vehicle_engine.model;

import java.math.BigDecimal;

public class Vehicle {
    private Long vehicleId;
    private BigDecimal vehiclePrice;

    public Vehicle() {
    }

    public Vehicle(Long vehicleId, BigDecimal vehiclePrice) {
        this.vehicleId = vehicleId;
        this.vehiclePrice = vehiclePrice;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public BigDecimal getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(BigDecimal vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
    }
}
