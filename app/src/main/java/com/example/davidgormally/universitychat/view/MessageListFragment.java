package com.example.davidgormally.universitychat.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.davidgormally.universitychat.Model.message.MessageContent;
import com.example.davidgormally.universitychat.Model.student.Student;
import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.NetworkTask.UpdateMessageListTask;
import com.example.davidgormally.universitychat.R;
import com.example.davidgormally.universitychat.controller.AppUserController;
import com.example.davidgormally.universitychat.controller.MessageController;
import com.example.davidgormally.universitychat.controller.StudentController;
import com.example.davidgormally.universitychat.service.FetchMessageService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageListFragment extends Fragment {

    private static final String ARG = "userId";
    private static final String TAG = "MessageListFragment";
    private RecyclerView recyclerView;
    private MessageListAdapter messageListAdapter;
    private String userId;

    public static MessageListFragment newInstance(String userId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG, userId);

        MessageListFragment messageListFragment = new MessageListFragment();
        messageListFragment.setArguments(bundle);

        return messageListFragment;
    }

    public MessageListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        userId = (String)getArguments().getSerializable(ARG);
        AppUserController appUserController = new AppUserController(getContext());
        AppUser appUser = appUserController.getAppUser(userId);

        //start service to retrieve messages from server on and off the app
        FetchMessageService.setAlarmForApp(getContext(), appUser.isUserSignedIn(), appUser.getAppUserId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        Log.d(TAG, "onCreateView");

        recyclerView = (RecyclerView)view.findViewById(R.id.message_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpAdapter();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        UpdateMessageListTask updateMessageListTask = new UpdateMessageListTask(getContext(), this, userId);
        Thread thread = new Thread(updateMessageListTask);
        thread.start();

        Log.d(TAG, "Background thread started");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setUpAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }





    private class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageListHolder> {

        private List<Student> students;

        //only receive list of students that have messages
        public MessageListAdapter(List<Student> students) {
            this.students = students;
        }

        @Override
        public MessageListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
            return new MessageListHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageListHolder holder, int position) {
            Student student = students.get(position);
            holder.bindStudentMessage(student);
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        public void setStudents(List<Student> students) {
            this.students = students;
        }

        class MessageListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private Student mStudent;
            private TextView studentNameTv;
            private TextView messageContentTv;
            private TextView dateTv;

            public MessageListHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                studentNameTv = (TextView)itemView.findViewById(R.id.name_tv);
                messageContentTv = (TextView)itemView.findViewById(R.id.message_tv);
                dateTv = (TextView)itemView.findViewById(R.id.date_tv);
            }

            public void bindStudentMessage(Student student) {
                mStudent = student;
                studentNameTv.setText(mStudent.getFirstName());

                MessageController messageController = new MessageController(getContext());
                List<MessageContent> messageContents = messageController.getMessages(mStudent.getStudentId());

                int lastMessage = messageContents.size() - 1;

                String messageContent = messageContents.get(lastMessage).getMessageContent();

                long date = messageContents.get(lastMessage).getMessageReceivedDate().getTime();
                String format = new SimpleDateFormat("MM/dd/yyyy").format(date);

                messageContentTv.setText(messageContent);
                dateTv.setText(format);
            }

            @Override
            public void onClick(View v) {
                Intent intent = MessageActivity.newIntent(getContext(), mStudent.getStudentId());
                startActivity(intent);
            }
        }
    }


    private void setUpAdapter() {
        if (isAdded()) {

            List<Student> studentsList = new ArrayList<>();

            List<String> studentIds = new ArrayList<>();

            StudentController studentController = new StudentController(getContext());
            List<Student> students = studentController.getStudents();

            for (int i = 0; i < students.size(); i++) {
                String studentId = students.get(i).getStudentId();
                studentIds.add(studentId);
            }

            List<String> messageStudentIds = new ArrayList<>();

            MessageController messageController = new MessageController(getContext());
            List<MessageContent> messageContents = messageController.getAllMessages();

            for (int i = 0; i < messageContents.size(); i++) {
                String studentId = messageContents.get(i).getStudentMessageBelongsTo();
                messageStudentIds.add(studentId);
            }

            for (int i = 0; i < studentIds.size(); i++) {
                if (messageStudentIds.contains(studentIds.get(i))) {
                    String studentId = studentIds.get(i);
                    Student student = studentController.getStudent(studentId);
                    studentsList.add(student);
                }
            }

            //adapter set up
            if (messageListAdapter == null) {
                recyclerView.setAdapter(new MessageListAdapter(studentsList));

            } else {
                messageListAdapter.notifyDataSetChanged();
                messageListAdapter.setStudents(studentsList);
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


}
