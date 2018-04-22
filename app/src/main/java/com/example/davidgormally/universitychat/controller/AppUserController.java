package com.example.davidgormally.universitychat.controller;

import android.content.Context;

import com.example.davidgormally.universitychat.Model.database.DatabaseApp;
import com.example.davidgormally.universitychat.Model.user.AppUser;

import java.util.List;


public class AppUserController {

    private DatabaseApp databaseApp;

    public AppUserController(Context context) {
        databaseApp = DatabaseApp.getInMemoryDatabase(context);
    }

    public void insertAppUser(AppUser appUser) {
        databaseApp.appUserModel().insertUser(appUser);
    }

    public void updateAppUser(AppUser appUser) {
        databaseApp.appUserModel().updateAppUser(appUser);
    }

    public List<AppUser> getAppUsers() {
        return databaseApp.appUserModel().getAppUsers();
    }

    public AppUser getAppUser(String id) {
        return databaseApp.appUserModel().getAppUser(id);
    }

}
