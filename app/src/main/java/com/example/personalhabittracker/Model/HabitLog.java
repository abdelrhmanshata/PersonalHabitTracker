package com.example.personalhabittracker.Model;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HabitLog {
    String ID, UID,habitID, habitTitle, status;
    long currentTimeMillis = System.currentTimeMillis();

    public HabitLog() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getHabitID() {
        return habitID;
    }

    public void setHabitID(String habitID) {
        this.habitID = habitID;
    }

    public String getHabitTitle() {
        return habitTitle;
    }

    public void setHabitTitle(String habitTitle) {
        this.habitTitle = habitTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentDate() {
        Date date = new Date(this.currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM");
        return dateFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {
        Date date = new Date(this.currentTimeMillis);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return timeFormat.format(date);
    }
}
