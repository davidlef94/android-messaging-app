package com.example.davidgormally.universitychat.Model.user;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class AppUser {

    @PrimaryKey
    @NonNull
    private String appUserId;

    @ColumnInfo(name = "first_name")
    private String appUserFirstName;

    @ColumnInfo(name = "last_name")
    private String appUserLastName;

    @ColumnInfo(name = "username")
    private String userName;

    @ColumnInfo(name = "email")
    private String appUserEmail;

    @ColumnInfo(name = "signed_in")
    private boolean userSignedIn;

    public AppUser() {

    }

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getAppUserFirstName() {
        return appUserFirstName;
    }

    public void setAppUserFirstName(String appUserFirstName) {
        this.appUserFirstName = appUserFirstName;
    }

    public String getAppUserLastName() {
        return appUserLastName;
    }

    public void setAppUserLastName(String appUserLastName) {
        this.appUserLastName = appUserLastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAppUserEmail() {
        return appUserEmail;
    }

    public void setAppUserEmail(String appUserEmail) {
        this.appUserEmail = appUserEmail;
    }

    public boolean isUserSignedIn() {
        return userSignedIn;
    }

    public void setUserSignedIn(boolean userSignedIn) {
        this.userSignedIn = userSignedIn;
    }
}
