package com.example.hackfrenzy;

public class User_Model {
    String email,name,online_Status,phone_Number,profile_Picture,username;

    public User_Model() {
    }

    public User_Model(String email, String name, String online_Status, String phone_Number, String profile_Picture, String username) {
        this.email = email;
        this.name = name;
        this.online_Status = online_Status;
        this.phone_Number = phone_Number;
        this.profile_Picture = profile_Picture;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_Number() {
        return phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        this.phone_Number = phone_Number;
    }

    public String getProfile_Picture() {
        return profile_Picture;
    }

    public void setProfile_Picture(String profile_Picture) {
        this.profile_Picture = profile_Picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOnline_Status() {
        return online_Status;
    }

    public void setOnline_Status(String online_Status) {
        this.online_Status = online_Status;
    }
}
