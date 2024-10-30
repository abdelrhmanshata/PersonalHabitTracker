package com.example.personalhabittracker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.personalhabittracker.Model.Habit;
import com.example.personalhabittracker.R;
import com.example.personalhabittracker.databinding.AddHabitBinding;
import com.example.personalhabittracker.databinding.DeleteMessageLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ManageHabits {

    ManageNotification manageNotification = new ManageNotification();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHabits = database.getReference("Habits");
    DatabaseReference refToDay = database.getReference("ToDay");
    ManageLogs manageLogs = new ManageLogs();


    @SuppressLint({"ObsoleteSdkInt", "DefaultLocale"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDialogAddHabitLayout(Habit currentHabit, Activity context) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_habit, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        AddHabitBinding addHabitBinding = AddHabitBinding.bind(dialogView);
        addHabitBinding.cardTitle.setText(currentHabit == null ? context.getString(R.string.add_habit) : context.getString(R.string.update_habit));

        Habit habit;
        String status;
        if (currentHabit == null) {
            habit = new Habit();
            status = "Added";
            habit.setUID(firebaseUser.getUid());
            habit.setID(String.valueOf(System.currentTimeMillis()));
            habit.setRepeat("Once");
            habit.setCompleted(false);
            habit.setDate(getToDay());
        } else {
            habit = currentHabit;
            status = "Edited";
            addHabitBinding.inputTitle.setText(habit.getTitle());
            addHabitBinding.inputDescription.setText(habit.getDescription());
            habit.setCompleted(habit.isCompleted());

            // set repeat
            addHabitBinding.once.setChecked(habit.getRepeat().equals("Once"));
            addHabitBinding.daily.setChecked(habit.getRepeat().equals("Daily"));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(habit.getTimeAtMillis()));

            addHabitBinding.timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            addHabitBinding.timePicker.setMinute(calendar.get(Calendar.MINUTE));
        }

        addHabitBinding.repeatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.daily) {
                    habit.setRepeat("Daily");
                } else {
                    habit.setRepeat("Once");
                }
            }
        });


        addHabitBinding.btnSave.setOnClickListener(v -> {
            String inputNoteTitle = Objects.requireNonNull(addHabitBinding.inputTitle.getText()).toString().trim();
            if (inputNoteTitle.isEmpty()) {
                addHabitBinding.inputTitle.setError(context.getString(R.string.habit_title_is_required));
                addHabitBinding.inputTitle.requestFocus();
                return;
            }
            String inputNoteDescription = Objects.requireNonNull(addHabitBinding.inputDescription.getText()).toString().trim();
            if (inputNoteDescription.isEmpty()) {
                addHabitBinding.inputDescription.setError(context.getString(R.string.habit_description_is_required));
                addHabitBinding.inputDescription.requestFocus();
                return;
            }

            int hour = addHabitBinding.timePicker.getHour();
            int minute = addHabitBinding.timePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 00);
            long TimeAtMillis = calendar.getTimeInMillis();

            habit.setTitle(inputNoteTitle);
            habit.setDescription(inputNoteDescription);
            habit.setTimeAtMillis(String.valueOf(TimeAtMillis));
            habit.setTime(getTime(hour, minute));

            // Save Habit
            saveHabit(habit, context, context.getString(R.string.save_habit_successfully), status);
            alertDialog.dismiss();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDialogDeleteMsgLayout(Habit currentHabit, Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        DeleteMessageLayoutBinding deleteMessageBinding = DeleteMessageLayoutBinding.bind(dialogView);
        deleteMessageBinding.inputMessage.setText(context.getString(R.string.are_your_sure_delete));
        deleteMessageBinding.buttonYes.setOnClickListener(v -> {

            refHabits.child(currentHabit.getUID()).child(currentHabit.getID()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.success(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show();
                    manageLogs.addLog(currentHabit, "Deleted");
                    alertDialog.dismiss();
                }
            });
        });

        deleteMessageBinding.buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveHabit(Habit habit, Context context, String message, String status) {
        refHabits.child(firebaseUser.getUid()).child(habit.getID()).setValue(habit).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toasty.success(context, message, Toast.LENGTH_SHORT).show();
                if (!status.contains("Checked")) {
                    manageLogs.addLog(habit, status);
                    manageNotification.addAlarmNotification(habit, context);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ResetDailyCheck() {
        refToDay.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date = dataSnapshot.getValue(String.class);
                if (date != null) {
                    if (!date.equals(getToDay())) {
                        refHabits.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Habit habit = snapshot.getValue(Habit.class);
                                    if (habit != null) {
                                        if ((habit.getRepeat().equals("Daily")) && (!habit.getDate().equals(getToDay()))) {
                                            refHabits.child(firebaseUser.getUid()).child(habit.getID()).child("completed").setValue(false);
                                            refHabits.child(firebaseUser.getUid()).child(habit.getID()).child("date").setValue(getToDay());
                                        }
                                    }
                                }
                                refToDay.child(firebaseUser.getUid()).setValue(getToDay());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                } else {
                    refToDay.child(firebaseUser.getUid()).setValue(getToDay());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return formatter.format(tme);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SimpleDateFormat", "NewApi"})
    private String getToDay() {
        return String.valueOf(LocalDate.now());
    }

}
