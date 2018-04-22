package com.example.davidgormally.universitychat.controller;

import android.content.Context;

import com.example.davidgormally.universitychat.Model.database.DatabaseApp;
import com.example.davidgormally.universitychat.Model.student.Student;

import java.util.List;


public class StudentController {

    private DatabaseApp databaseApp;

    public StudentController(Context context) {
        databaseApp = DatabaseApp.getInMemoryDatabase(context);
    }

    public void addStudent(Student student) {
        databaseApp.studentModel().insertStudent(student);
    }

    public void updateStudent(Student student) {
        databaseApp.studentModel().updateStudent(student);
    }

    public void deleteStudent(Student student) {
        databaseApp.studentModel().deleteStudent(student);
    }

    public List<Student> getStudents() {
        return databaseApp.studentModel().getStudents();
    }

    public Student getStudent(String id) {
        return databaseApp.studentModel().getStudent(id);
    }
}
