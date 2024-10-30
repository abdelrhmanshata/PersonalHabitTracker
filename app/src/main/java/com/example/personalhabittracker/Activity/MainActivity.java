package com.example.personalhabittracker.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalhabittracker.Fragment.DoneFragment;
import com.example.personalhabittracker.Fragment.HistoryFragment;
import com.example.personalhabittracker.Fragment.HomeFragment;
import com.example.personalhabittracker.Fragment.ProfileFragment;
import com.example.personalhabittracker.R;
import com.example.personalhabittracker.databinding.ActivityMainBinding;
import com.example.personalhabittracker.utils.ManageHabits;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHabits = database.getReference("Habits");

    ManageHabits manageHabits;

    ActivityMainBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        requestPermissions();

        manageHabits = new ManageHabits();

        manageHabits.ResetDailyCheck();

        //default navigation
        Home();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.done) {
                Done();
            } else if (item.getItemId() == R.id.history) {
                History();
            } else if (item.getItemId() == R.id.profile) {
                Profile();
            } else {
                Home();
            }
            return true;
        });

        binding.addNewHabit.setOnClickListener(v -> {
            manageHabits.showDialogAddHabitLayout(null, this);
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("SetTextI18n")
    private void Home() {
        replaceFragment(new HomeFragment());
        binding.toolbarText.setText(getString(R.string.home));
    }

    @SuppressLint("SetTextI18n")
    private void Done() {
        replaceFragment(new DoneFragment());
        binding.toolbarText.setText(getString(R.string.done));
    }

    @SuppressLint("SetTextI18n")
    private void History() {
        replaceFragment(new HistoryFragment());
        binding.toolbarText.setText(getString(R.string.history));
    }

    @SuppressLint("SetTextI18n")
    private void Profile() {
        replaceFragment(new ProfileFragment());
        binding.toolbarText.setText(getString(R.string.profile));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
}