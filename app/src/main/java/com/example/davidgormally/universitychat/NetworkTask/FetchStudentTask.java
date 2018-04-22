package com.example.davidgormally.universitychat.NetworkTask;

import android.content.Context;
import android.util.Log;

import com.example.davidgormally.universitychat.Model.student.Student;
import com.example.davidgormally.universitychat.controller.StudentController;
import com.example.davidgormally.universitychat.view.StudentListFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class FetchStudentTask implements Runnable {

    private static final String TAG = "student task";
    private Context context;
    private boolean running = false;
    private StudentListFragment studentListFragment;

    public FetchStudentTask(Context context, StudentListFragment studentListFragment) {
        this.context = context;
        this.studentListFragment = studentListFragment;
        this.running = true;
    }

    @Override
    public void run() {

        Socket socket = null;

        try {

            Inet4Address address = (Inet4Address)Inet4Address.getByName("address");
            socket = new Socket(address, 1248);

            PrintWriter output = new PrintWriter(socket.getOutputStream());
            output.println("GetStudentList");
            output.flush();


            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //maybe is added
            while (running) {
                String resultInput = input.readLine();

                if (resultInput.equals("Finished")) {
                    running = false;

                } else if (resultInput.contains("StudentData")){

                    String[] splitResult = resultInput.split("-");
                    String id = splitResult[1];
                    String firstName = splitResult[2];
                    String lastName = splitResult[3];
                    String email = splitResult[4];

                    //could put this in a separate method
                    List<String> ids = new ArrayList<>();
                    StudentController studentController = new StudentController(context);
                    List<Student> students = studentController.getStudents();
                    for (int i = 0; i < students.size(); i++) {
                        String studentId = students.get(i).getStudentId();
                        ids.add(studentId);
                    }



                    if (!ids.contains(id)) {
                        //create new student object
                        Student student = new Student();
                        student.setStudentId(id);
                        student.setFirstName(firstName);
                        student.setLastName(lastName);
                        student.setEmail(email);

                        studentController.addStudent(student);
                    }

                }


            }

            studentListFragment.updateUIFromThread();


        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    Log.e(TAG, "socket closed");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
