package com.example.personalhabittracker.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.personalhabittracker.Model.Habit;
import com.example.personalhabittracker.Model.HabitLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageLogs {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refLogs = database.getReference("Logs");


    @RequiresApi(api = Build.VERSION_CODES.O)
    void addLog(Habit habit, String status) {
        HabitLog log = new HabitLog();
        log.setID(refLogs.push().getKey());
        log.setUID(firebaseUser.getUid());
        log.setHabitID(habit.getID());
        log.setHabitTitle(habit.getTitle());
        log.setStatus(status);
        refLogs.child(log.getUID()).child(log.getID()).setValue(log);
    }
}
