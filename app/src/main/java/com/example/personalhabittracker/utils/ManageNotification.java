package com.example.personalhabittracker.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.personalhabittracker.Broadcast_Receiver.AlarmReceiver;
import com.example.personalhabittracker.Model.Habit;

public class ManageNotification {


    @SuppressLint("ScheduleExactAlarm")
    public void addAlarmNotification(Habit habit, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Create the intent that will be fired when the alarm triggers
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("Habit_ID", habit.getID());
        // Create the PendingIntent to pass to the AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) Long.parseLong(habit.getID()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (habit.getRepeat().equals("Daily")) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Long.parseLong(habit.getTimeAtMillis()), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Long.parseLong(habit.getTimeAtMillis()), pendingIntent);
        }
    }
}
