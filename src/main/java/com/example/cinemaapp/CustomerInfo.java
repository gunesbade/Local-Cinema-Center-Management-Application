package com.example.cinemaapp;

public class CustomerInfo {
    private String name;
    private String birthDate;
    private int age; // Yeni bir age alanı ekliyoruz

    // Argümanlı constructor
    public CustomerInfo(String name, String birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    // Varsayılan constructor
    public CustomerInfo() {
    }

    // Getter ve Setter'lar
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    // Yeni age alanı için getter ve setter
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

