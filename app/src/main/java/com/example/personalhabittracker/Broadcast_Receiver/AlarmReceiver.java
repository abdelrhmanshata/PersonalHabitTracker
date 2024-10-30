package com.example.personalhabittracker.Broadcast_Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.personalhabittracker.Activity.MainActivity;
import com.example.personalhabittracker.Model.Habit;
import com.example.personalhabittracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlarmReceiver extends BroadcastReceiver {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser CurrentUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHabits = database.getReference("Habits");

    Habit habit = null;
    Context context;
    String ID = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        boolean connection = isNetworkAvailable(context);
        ID = intent.getStringExtra("Habit_ID");
        if (connection) {
            refHabits.child(CurrentUser.getUid()).child(String.valueOf(ID)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    habit = snapshot.getValue(Habit.class);
                    if (habit != null) {
                        pushNotification(context, habit, ID);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Push the notification when the alarm triggers
            pushNotification(context, null, ID);
        }


    }

    private void pushNotification(Context context, Habit habit, String ID) {

        String title = "You have an alert " + (habit != null ? ": "+habit.getTitle() : "") + ".";
        String description = (habit != null ? "Alert Description : " + habit.getDescription() : "");

        // Create the notification channel (required for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("AlarmNotification", "Alarm Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notification for Alarm");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the intent to open the app when the notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmNotification").setSmallIcon(R.drawable.logo) // Use your app's icon here
                .setContentTitle(title).setContentText(description).setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent).setAutoCancel(true);  // Dismiss the notification when clicked

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Request permission for notifications
                return;
            }
        }
        notificationManager.notify((int) Long.parseLong(ID), builder.build());
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
