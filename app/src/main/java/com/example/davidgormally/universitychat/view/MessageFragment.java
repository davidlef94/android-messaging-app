package com.example.davidgormally.universitychat.view;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.davidgormally.universitychat.Model.message.MessageContent;
import com.example.davidgormally.universitychat.Model.student.Student;
import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.NetworkTask.SendMessageTask;
import com.example.davidgormally.universitychat.R;
import com.example.davidgormally.universitychat.controller.AppUserController;
import com.example.davidgormally.universitychat.controller.MessageController;
import com.example.davidgormally.universitychat.controller.StudentController;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    private static final String arg = "student_id";
    private Student mStudent;
    private AppUser appUser;
    private EditText messageContentEt;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private SendMessageTask sendMessageTask;


    public static MessageFragment newInstance(String studentId) {
        Bundle args = new Bundle();
        args.putSerializable(arg, studentId);

        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(args);

        return messageFragment;
    }


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        //student controller
        StudentController studentController = new StudentController(getContext());
        String studentId = (String)getArguments().getSerializable(arg);
        mStudent = studentController.getStudent(studentId);

        AppUserController appUserController = new AppUserController(getContext());
        List<AppUser> appUsers = appUserController.getAppUsers();

        String id = null;

        for (int i = 0; i < appUsers.size(); i++) {
            if (appUsers.get(i).isUserSignedIn()) {
                id = appUsers.get(i).getAppUserId();
            }
        }

        appUser = appUserController.getAppUser(id);

        sendMessageTask = new SendMessageTask(this, getContext(), appUser.getAppUserId());
        Thread thread = new Thread(sendMessageTask);
        thread.start();
        Log.d(TAG, "Background thread created");

        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        Log.d(TAG, "onCreateView");

        recyclerView = (RecyclerView)view.findViewById(R.id.message_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        messageContentEt = (EditText)view.findViewById(R.id.message_content_edit_text);

        FloatingActionButton sendFab = (FloatingActionButton)view.findViewById(R.id.send_message_fab);
        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        setUpAdapter();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.delete_chat:

                String id = (String)getArguments().getSerializable(arg);
                StudentController studentController = new StudentController(getContext());
                mStudent = studentController.getStudent(id);
                studentController.deleteStudent(mStudent);

                MessageController messageController = new MessageController(getContext());
                List<MessageContent> messageContents = messageController.getMessages(mStudent.getStudentId());
                for (int i = 0; i < messageContents.size(); i++) {
                    messageController.deleteMessage(messageContents.get(i));
                }

                getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

        private List<MessageContent> messageContents;

        public MessageAdapter(List<MessageContent> messageContents) {
            this.messageContents = messageContents;
        }

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new MessageHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            MessageContent messageContent = messageContents.get(position);
            holder.bindMessageContent(messageContent);
        }

        @Override
        public int getItemCount() {
            return messageContents.size();
        }

        public void setMessageContents(List<MessageContent> messageContents) {
            this.messageContents = messageContents;
        }

        class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView dateTv;
            private TextView messageTv;
            private MessageContent messageContent;

            public MessageHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                dateTv = (TextView)itemView.findViewById(R.id.message_date_tv);
                messageTv = (TextView)itemView.findViewById(R.id.message_content_tv);
            }

            public void bindMessageContent(MessageContent messageContent) {
                this.messageContent = messageContent;

                long date = messageContent.getMessageReceivedDate().getTime();
                String dateFormat = new SimpleDateFormat("MM/dd/yyyy").format(date);
                dateTv.setText(dateFormat);

                messageTv.setText(messageContent.getMessageContent());
            }

            @Override
            public void onClick(View v) {
            }
        }
    }




    private void setUpAdapter() {

        if (isAdded()) {
            MessageController messageController = new MessageController(getContext());
            List<MessageContent> messageContents = messageController.getMessages(mStudent.getStudentId());

            if (messageAdapter == null) {
                recyclerView.setAdapter(new MessageAdapter(messageContents));

            } else {
                messageAdapter.notifyDataSetChanged();
                messageAdapter.setMessageContents(messageContents);
            }
        }
    }

    public void updateUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUpAdapter();
            }
        });
    }


    private void sendMessage() {
        String studentId = mStudent.getStudentId();
        String messageContent = messageContentEt.getText().toString();
        String senderId = appUser.getAppUserId();

        String result = studentId + "-" + messageContent + "-" + senderId;

        Message message = sendMessageTask.sendMessageHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("SendKey", result);
        message.setData(bundle);

        sendMessageTask.sendMessageHandler.sendMessage(message);
    }



}
