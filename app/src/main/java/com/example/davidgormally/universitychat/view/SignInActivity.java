package com.example.davidgormally.universitychat.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.davidgormally.universitychat.NetworkTask.SignInTask;
import com.example.davidgormally.universitychat.R;


public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private EditText usernameET;
    private EditText passwordET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Log.d(TAG, "onCreate()");

        usernameET = (EditText)findViewById(R.id.username_et);
        passwordET = (EditText)findViewById(R.id.password_et);

        Button signInButton = (Button)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUserIn();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }



    private void signUserIn() {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        final String resultToSend = username + "-" + password;

        if (username.equals("") && password.equals("")) {
            Toast.makeText(this, "Please enter data", Toast.LENGTH_SHORT).show();

        } else {
            SignInTask signInTask = new SignInTask(resultToSend, this, getApplicationContext());
            Thread thread = new Thread(signInTask);
            thread.start();
            Log.d(TAG, "Thread Started");
        }

    }


    public void signInResult(String command, String userId) {

        if (command.contains("Successful")) {
            Intent intent = TabActivity.newIntent(this, userId);
            startActivity(intent);

        } else if (command.equals("Unsuccessful")) {

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Incorrect Details", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
