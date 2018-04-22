package com.example.davidgormally.universitychat.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.controller.AppUserController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckOnlineStatus extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUserController appUserController = new AppUserController(this);
        List<AppUser> appUsers = appUserController.getAppUsers();

        Map<Boolean, String> onlineStatusMap = new HashMap<>();

        for (int i = 0; i < appUsers.size(); i++) {
            if (appUsers.get(i).isUserSignedIn()) {
                boolean signedIn = appUsers.get(i).isUserSignedIn();
                String appUserId = appUsers.get(i).getAppUserId();
                onlineStatusMap.put(signedIn, appUserId);
            }
        }

        if (!onlineStatusMap.isEmpty()) {
            for (Map.Entry<Boolean, String> appUser : onlineStatusMap.entrySet()) {
                if (appUser.getKey()) {
                    String id = appUser.getValue();
                    Intent intent = TabActivity.newIntent(this, id);
                    startActivity(intent);

                }
            }

        } else if (onlineStatusMap.isEmpty()) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }
}
