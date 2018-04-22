package com.example.davidgormally.universitychat.Model.user;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface AppUserDao {

    @Insert
    void insertUser(AppUser appUser);

    @Update
    void updateAppUser(AppUser appUser);

    @Query("select * from appuser")
    List<AppUser> getAppUsers();

    @Query("select * from appuser where appUserId =:id")
    AppUser getAppUser(String id);

}
