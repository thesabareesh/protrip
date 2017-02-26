package me.sabareesh.trippie.model;

import android.net.Uri;

/**
 * Created by Sabareesh on 14-Feb-17.
 */

public class User {
    private String username;
    private Uri avatarUrl;
    private String emailId;
    private String uid;

    public User() {

    }

    public User(String username, Uri avatarUrl, String emailId, String uid) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.emailId = emailId;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Uri getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(Uri avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
