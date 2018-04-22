package com.example.davidgormally.universitychat.NetworkTask;

import android.content.Context;

import com.example.davidgormally.universitychat.Model.message.MessageContent;
import com.example.davidgormally.universitychat.controller.MessageController;
import com.example.davidgormally.universitychat.view.MessageListFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpdateMessageListTask implements Runnable {

    private Context context;
    private MessageListFragment messageListFragment;
    private String userId;
    private boolean running = false;
    private List<String> messageIds = new ArrayList<>();

    public UpdateMessageListTask(Context context, MessageListFragment messageListFragment, String userId) {
        this.context = context;
        this.messageListFragment = messageListFragment;
        this.userId = userId;
        this.running = true;
    }

    @Override
    public void run() {

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

                    MessageController messageController = new MessageController(context);
                    messageController.insertMessage(messageContent);

                    messageIds.add(messageId);

                }

            }


            messageListFragment.updateUI();

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

            try{
                if (socket != null) {
                    socket.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
