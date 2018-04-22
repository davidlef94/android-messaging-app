package com.example.davidgormally.universitychat.NetworkTask;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.davidgormally.universitychat.Model.message.MessageContent;
import com.example.davidgormally.universitychat.controller.MessageController;
import com.example.davidgormally.universitychat.view.MessageFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

public class SendMessageTask implements Runnable {

    private static final String TAG = "SendMessageTask";
    private MessageFragment messageFragment;
    private Context context;
    private PrintWriter output;
    private String appUserId;

    public SendMessageTask(MessageFragment messageFragment, Context context, String appUserId) {
        this.messageFragment = messageFragment;
        this.context = context;
        this.appUserId = appUserId;
    }

    @Override
    public void run() {

        Socket socket = null;

        try {

            Inet4Address address = (Inet4Address)Inet4Address.getByName("192.168.0.7");
            socket = new Socket(address, 1248);

            output = new PrintWriter(socket.getOutputStream());
            output.println("SendMessage" + "-" + appUserId);
            output.flush();

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (!messageFragment.getActivity().isDestroyed()) {

                String inputResult = input.readLine();

                String[] splitResult = inputResult.split("-");
                String messageId = splitResult[0];
                String content = splitResult[1];
                String senderId = splitResult[2];

                MessageContent messageContent = new MessageContent();
                messageContent.setMessageId(messageId);
                messageContent.setMessageContent(content);
                messageContent.setStudentMessageBelongsTo(senderId);
                messageContent.setMessageReceivedDate(new Date());

                MessageController messageController = new MessageController(context);
                messageController.insertMessage(messageContent);

                //confirm message has been received
                output.println("MessageReceived" + "-" + messageId);
                output.flush();

                //update the UI
                messageFragment.updateUI();

            }

            output.println("ExitChat");
            output.flush();


        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                if (socket != null) {
                    socket.close();
                    Log.d(TAG, "socket closed");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    //send message through handler
    @SuppressLint("HandlerLeak")
    public Handler sendMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String messageContent = bundle.getString("SendKey");

            output.println("SendContent" + "-" + messageContent);
            output.flush();

            //saving message on device using UUID for the message id
            String[] splitData = messageContent.split("-");
            String studentId = splitData[0];
            String content = splitData[1];

            MessageContent messageContent1 = new MessageContent();
            messageContent1.setMessageId(UUID.randomUUID().toString());
            messageContent1.setStudentMessageBelongsTo(studentId);
            messageContent1.setMessageContent(content);
            messageContent1.setMessageReceivedDate(new Date());

            MessageController messageController = new MessageController(context);
            messageController.insertMessage(messageContent1);

            messageFragment.updateUI();
        }
    };


}
