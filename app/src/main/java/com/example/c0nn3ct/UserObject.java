package com.example.c0nn3ct;

public class UserObject {

    private String name, phone;

    UserObject(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    String getPhone() {
        return phone;
    }

    void setPhone(String phone) {
        this.phone = phone;
    }
}
