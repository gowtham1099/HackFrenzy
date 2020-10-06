package com.example.hackfrenzy;

public class Status_Model {
    String description,email,tittle,timeStamp,video_url;

    public Status_Model() {
    }

    public Status_Model(String description, String email, String tittle, String timeStamp, String video_url) {
        this.description = description;
        this.email = email;
        this.tittle = tittle;
        this.timeStamp = timeStamp;
        this.video_url = video_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
