package com.example.davidgormally.universitychat.NetworkTask;


import android.content.Context;
import android.util.Log;

import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.controller.AppUserController;
import com.example.davidgormally.universitychat.view.SignInActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.List;

public class SignInTask implements Runnable {

    private static final String TAG = "SignInTask";
    private String signInResult;
    private SignInActivity signInActivity;
    private Context context;

    public SignInTask(String signInResult, SignInActivity signInActivity, Context context) {
        this.signInResult = signInResult;
        this.signInActivity = signInActivity;
        this.context = context;
    }

    @Override
    public void run() {
        Socket socket = null;

        try {
            Inet4Address address = (Inet4Address)Inet4Address.getByName("192.168.0.7");
            socket = new Socket(address, 1248);

            Log.d(TAG, "Connected to server!!");

            PrintWriter output = new PrintWriter(socket.getOutputStream());
            output.println("SignInUser" + "-" + signInResult);
            output.flush();

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String result = input.readLine();

            if (result.contains("Successful")) {
                //split result to obtain user info
                String[] splitResult = result.split("-");
                String success = splitResult[0];
                String userId = splitResult[1];
                String fistName = splitResult[2];
                String lastName = splitResult[3];
                String userName = splitResult[4];
                String email = splitResult[5];

                String locatedId = null;

                AppUserController appUserController = new AppUserController(context);
                List<AppUser> appUsers = appUserController.getAppUsers();
                for (int i = 0; i  < appUsers.size(); i++) {
                    if (appUsers.get(i).getAppUserId().equals(userId)) {
                        locatedId = appUsers.get(i).getAppUserId();
                    }
                }

                if (locatedId == null) {
                    //if new app user
                    AppUser appUser = new AppUser();
                    appUser.setAppUserId(userId);
                    appUser.setAppUserFirstName(fistName);
                    appUser.setAppUserLastName(lastName);
                    appUser.setUserName(userName);
                    appUser.setAppUserEmail(email);
                    appUser.setUserSignedIn(true);

                    appUserController.insertAppUser(appUser);
                    Log.d(TAG, "user added");

                } else {
                    AppUser appUser = appUserController.getAppUser(userId);
                    appUser.setUserSignedIn(true);
                    appUserController.updateAppUser(appUser);
                }

                signInActivity.signInResult(success, userId);


            } else if (result.contains("Unsuccessful")) {
                String[] splitResult = result.split("-");
                String unsuccessful = splitResult[0];
                signInActivity.signInResult(unsuccessful, null);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
