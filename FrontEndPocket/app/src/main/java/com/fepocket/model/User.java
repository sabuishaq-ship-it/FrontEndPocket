package com.fepocket.model;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private byte[] profileImage;

    public User() {}

    public User(String fullName, String username, String email, String password, byte[] profileImage) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public User(int id, String fullName, String username, String email, String password, byte[] profileImage) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }
}
