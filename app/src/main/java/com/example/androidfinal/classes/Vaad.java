package com.example.androidfinal.classes;

public class Vaad extends User {
    private int seniority;

    public Vaad(String userName, String firstName, String surname, int id, int seniority) {
        super(userName, firstName, surname, id, "vaad");
        this.seniority = seniority;
    }

    public Vaad() {
    }

    public int getSeniority() {
        return seniority;
    }

    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }
}
