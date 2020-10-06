package com.example.hackfrenzy;

public class Notification_Model {
    String description,image,tittle,timeStamp,url;

    public Notification_Model() {
    }

    public Notification_Model(String description,String image, String timeStamp, String tittle, String url) {
        this.description = description;
        this.image=image;
        this.timeStamp = timeStamp;
        this.tittle = tittle;
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
