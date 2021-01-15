package com.example.androidfinal.classes;

public abstract class User {
    private String userName;
    private String firstName;
    private String surname;
    private int id;
    private String userType;

    public User(String userName, String firstName, String surname, int id) {
        this.userName = userName;
        this.firstName = firstName;
        this.surname = surname;
        this.id = id;
    }

    public User(String userName, String firstName, String surname, int id, String userType) {
        this.userName = userName;
        this.firstName = firstName;
        this.surname = surname;
        this.id = id;
        this.userType = userType;
    }

    public User() {
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
