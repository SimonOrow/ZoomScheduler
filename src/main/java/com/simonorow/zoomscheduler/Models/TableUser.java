package com.simonorow.zoomscheduler.Models;

public class TableUser {

    private String name = null;
    private String email = null;

    public TableUser() {
    }

    public TableUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

