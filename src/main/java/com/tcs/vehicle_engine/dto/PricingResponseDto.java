package com.tcs.vehicle_engine.dto;

import java.math.BigDecimal;

public class PricingResponseDto {
    private Long vehicleId;
    private BigDecimal vehiclePrice;
    private BigDecimal downPayment;
    private BigDecimal apr;
    private Integer loanTerm;
    private BigDecimal monthlyPayment;

    public PricingResponseDto() {
    }

    public PricingResponseDto(Long vehicleId, BigDecimal vehiclePrice, BigDecimal downPayment, BigDecimal apr, Integer loanTerm, BigDecimal monthlyPayment) {
        this.vehicleId = vehicleId;
        this.vehiclePrice = vehiclePrice;
        this.downPayment = downPayment;
        this.apr = apr;
        this.loanTerm = loanTerm;
        this.monthlyPayment = monthlyPayment;
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

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = downPayment;
    }

    public BigDecimal getApr() {
        return apr;
    }

    public void setApr(BigDecimal apr) {
        this.apr = apr;
    }

    public Integer getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(Integer loanTerm) {
        this.loanTerm = loanTerm;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
}
