package com.example.davidgormally.universitychat.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.davidgormally.universitychat.Model.message.MessageContent;
import com.example.davidgormally.universitychat.Model.student.Student;
import com.example.davidgormally.universitychat.R;
import com.example.davidgormally.universitychat.controller.MessageController;
import com.example.davidgormally.universitychat.controller.StudentController;
import com.example.davidgormally.universitychat.view.TabActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FetchMessageService extends IntentService{

    private static final String TAG = "FetchMessageService";
    private static final int POLL_INTERVAL = 1000 * 60; //60 seconds
    private static final String CHANNEL_ID = "Service_Channel";

    private static String userId;

    private List<String> messageIds = new ArrayList<>();

    public static Intent newIntent(Context context, String id) {
        userId = id;
        return new Intent(context, FetchMessageService.class);
    }

    public FetchMessageService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!isNetworkAvailableAndConnected()) {
            return;
        }


        boolean running = true;

        //connect to server to see if any new messages are available
        Socket socket = null;
        try {

            Inet4Address address = (Inet4Address)Inet4Address.getByName("192.168.0.7");
            socket = new Socket(address, 1248);

            PrintWriter output = new PrintWriter(socket.getOutputStream());
            output.println("UpdateMessageList" + "-" + userId);
            output.flush();

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (running) {

                String inputResult = input.readLine();

                if (inputResult.equals("Finished")) {
                    running = false;

                } else {

                    String[] splitResult = inputResult.split("-");
                    String messageId = splitResult[0];
                    String content = splitResult[1];
                    String senderId = splitResult[2];



                    MessageContent messageContent = new MessageContent();
                    messageContent.setMessageId(messageId);
                    messageContent.setMessageContent(content);
                    messageContent.setStudentMessageBelongsTo(senderId);
                    messageContent.setMessageReceivedDate(new Date());

                    MessageController messageController = new MessageController(this);
                    messageController.insertMessage(messageContent);

                    messageIds.add(messageId);


                    String studentName = null;
                    //grab sender id to then be used to locate the name of the sender from table
                    StudentController studentController = new StudentController(this);
                    List<Student> students = studentController.getStudents();
                    for (int i = 0; i < students.size(); i++) {
                        if (students.get(i).getStudentId().equals(senderId)) {
                            studentName = students.get(i).getFirstName() + "" + students.get(i).getLastName();
                            break;
                        }
                    }

                    //alert user (if signed in) to the new notifications
                    Intent i = TabActivity.newIntent(this, userId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

                    Notification notification =
                            new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notification)
                                    .setContentTitle(studentName)
                                    .setContentText(content)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build();

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                    notificationManagerCompat.notify(0, notification);

                }

            }

            //send message ids to confirm they have been received and therefore the server will
            //remove them
            for (int i = 0; i < messageIds.size(); i++) {
                output.println(messageIds.get(i));
                output.flush();
            }

            output.println("Completed");
            output.flush();


        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {

                if (socket != null) {
                    socket.close();
                    Log.d(TAG, "Service Socket closed");
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        Log.i(TAG, "Received an intent: " + intent);
    }


    public static void setAlarmForApp(Context context, boolean studentSignedIn, String appUserId) {
        Intent intent = FetchMessageService.newIntent(context, appUserId);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (studentSignedIn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);

        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }


    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
