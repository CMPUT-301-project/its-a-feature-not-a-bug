package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String role;

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setRole(String role) {
        this.role = role;
    }

}
