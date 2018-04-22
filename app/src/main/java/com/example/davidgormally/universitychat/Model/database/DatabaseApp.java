package com.example.davidgormally.universitychat.Model.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.davidgormally.universitychat.Model.message.MessageContent;
import com.example.davidgormally.universitychat.Model.message.MessageContentDao;
import com.example.davidgormally.universitychat.Model.student.Student;
import com.example.davidgormally.universitychat.Model.student.StudentDao;
import com.example.davidgormally.universitychat.Model.user.AppUser;
import com.example.davidgormally.universitychat.Model.user.AppUserDao;

@Database(entities = {AppUser.class, Student.class, MessageContent.class}, version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter.class)
public abstract class DatabaseApp extends RoomDatabase {

    private static DatabaseApp INSTANCE;
    public abstract AppUserDao appUserModel();
    public abstract StudentDao studentModel();
    public abstract MessageContentDao messageContentModel();

    public static DatabaseApp getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, DatabaseApp.class, "userStudentAndMessage.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;
    }

}
