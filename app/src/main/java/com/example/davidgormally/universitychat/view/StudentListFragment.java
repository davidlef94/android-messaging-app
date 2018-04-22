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

import com.example.davidgormally.universitychat.Model.student.Student;
import com.example.davidgormally.universitychat.NetworkTask.FetchStudentTask;
import com.example.davidgormally.universitychat.R;
import com.example.davidgormally.universitychat.controller.StudentController;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentListFragment extends Fragment {

    private static final String TAG = "StudentListFragment";
    private RecyclerView recyclerView;
    private StudentListAdapter studentListAdapter;


    public StudentListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        Log.d(TAG, "onCreateView");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_student_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateList();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        FetchStudentTask fetchStudentTask = new FetchStudentTask(getContext(), this);
        Thread thread = new Thread(fetchStudentTask);
        thread.start();
        Log.i(TAG, "Background Thread Started");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        updateList();
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



    //holder and adapter
    private class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListHolder> {

        private List<Student> students;

        public StudentListAdapter(List<Student> students) {
            this.students = students;
        }

        @Override
        public StudentListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
            return new StudentListHolder(view);
        }

        @Override
        public void onBindViewHolder(StudentListHolder holder, int position) {
            Student student = students.get(position);
            holder.bindStudent(student);
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        public void setStudents(List<Student> students) {
            this.students = students;
        }


        class StudentListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView firstNameTv;
            private TextView lastNameTv;
            private TextView emailTv;
            private Student mStudent;

            public StudentListHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                firstNameTv = (TextView)itemView.findViewById(R.id.student_first_name);
                lastNameTv = (TextView)itemView.findViewById(R.id.student_last_name);
                emailTv = (TextView)itemView.findViewById(R.id.student_email);
            }

            public void bindStudent(Student student) {
                mStudent = student;
                firstNameTv.setText(mStudent.getFirstName());
                lastNameTv.setText(mStudent.getLastName());
                emailTv.setText(mStudent.getEmail());
            }

            @Override
            public void onClick(View v) {
                Intent intent = MessageActivity.newIntent(getContext(), mStudent.getStudentId());
                startActivity(intent);
            }
        }
    }



    //update UI
    private void updateList() {

        if (isAdded()) {

            StudentController studentController = new StudentController(getContext());
            List<Student> students = studentController.getStudents();

            if (studentListAdapter == null) {
                recyclerView.setAdapter(new StudentListAdapter(students));

            } else {
                studentListAdapter.notifyDataSetChanged();
                studentListAdapter.setStudents(students);
            }
        }

    }


    public void updateUIFromThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateList();
            }
        });

    }


}
