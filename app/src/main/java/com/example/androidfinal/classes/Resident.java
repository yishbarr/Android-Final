package com.example.androidfinal.classes;

import java.util.HashMap;

public class Resident extends User {
    private String vaadUid;
    private String vaadEmail;
    private int addressNumber;
    private HashMap<String, Double> monthlyPayments;

    public Resident(String userName, String firstName, String surname, int id, int addressNumber, String vaadEmail, String vaadUid) {
        super(userName, firstName, surname, id, "resident");
        this.addressNumber = addressNumber;
        this.vaadEmail = vaadEmail;
        this.vaadUid = vaadUid;
        this.monthlyPayments = new HashMap<>();
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

    public void setMonthlyPayments(HashMap<String, Double> monthlyPayments) {
        this.monthlyPayments = monthlyPayments;
    }

    public String getVaadEmail() {
        return vaadEmail;
    }

    public void setVaadEmail(String vaadEmail) {
        this.vaadEmail = vaadEmail;
    }
}
