package com.example.androidfinal.classes;

import java.time.Month;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Resident extends User {
    private int addressNumber;
    private LinkedHashMap<String, Double> monthlyPayments;

    public Resident(String userName, String firstName, String surname, int id, int addressNumber) {
        super(userName, firstName, surname, id, "resident");
        this.addressNumber = addressNumber;
        this.monthlyPayments = new LinkedHashMap<>();
        for (String month : new String[]{"January", "Febuary", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"})
            this.monthlyPayments.put(month, 0.0);
    }

    public Resident() {
    }

    public int getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }

    public HashMap<String, Double> getMonthlyPayments() {
        return monthlyPayments;
    }

    public void setMonthlyPayments(LinkedHashMap<String, Double> monthlyPayments) {
        this.monthlyPayments = monthlyPayments;
    }
}
