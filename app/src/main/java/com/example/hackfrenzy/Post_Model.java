package com.example.hackfrenzy;

public class Post_Model {
    String description,email,image_Url,likes,profile_Picture,timeStamp,username;

    public Post_Model() {
    }

    public Post_Model(String description, String email, String image_Url, String likes, String profile_Picture, String timeStamp, String username) {
        this.description = description;
        this.email = email;
        this.image_Url = image_Url;
        this.likes = likes;
        this.profile_Picture = profile_Picture;
        this.timeStamp = timeStamp;
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_Url() {
        return image_Url;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getProfile_Picture() {
        return profile_Picture;
    }

    public void setProfile_Picture(String profile_Picture) {
        this.profile_Picture = profile_Picture;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
