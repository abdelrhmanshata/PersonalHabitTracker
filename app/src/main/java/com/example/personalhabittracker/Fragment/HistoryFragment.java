package com.example.personalhabittracker.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.personalhabittracker.Model.HabitLog;
import com.example.personalhabittracker.R;
import com.example.personalhabittracker.databinding.FragmentHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refLogs = database.getReference("Logs");

    ArrayList<HabitLog> mHabitLogs;


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        init();
        getAllHabitsLogs();

        return binding.getRoot();
    }

    void init() {
        mHabitLogs = new ArrayList<>();
    }

    void getAllHabitsLogs() {
        binding.progressBar.setVisibility(View.VISIBLE);
        refLogs.child(firebaseUser.getUid()).orderByChild("currentTimeMillis").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHabitLogs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HabitLog log = snapshot.getValue(HabitLog.class);
                    if (log != null) {
                        mHabitLogs.add(log);
                    }
                }

                if (isAdded()) {
                    // Reverse the list to display it in descending order
                    Collections.reverse(mHabitLogs);
                    for (HabitLog log : mHabitLogs) {
                        binding.logsLayout.addView(createRow(log));
                    }
                }

                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    TableRow createRow(HabitLog log) {
        if (!isAdded()) {
            return null;
        }

        // Create a new TableRow
        TableRow tableRow = new TableRow(requireContext());

        // Create and add TextViews to the row
        tableRow.addView(createTextView(log.getHabitTitle()));
        tableRow.addView(createTextView(log.getStatus()));
        tableRow.addView(createTextView(log.getCurrentTime()));
        tableRow.addView(createTextView(log.getCurrentDate()));

        return tableRow;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    TextView createTextView(String text) {
        if (!isAdded()) {
            return null;
        }
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setMaxLines(1);
        textView.setTextColor(requireContext().getColor(getColor(text)));
        textView.setLayoutParams(setTableRowWeight());
        textView.setBackground(requireContext().getDrawable(R.drawable.table_border));
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    TableRow.LayoutParams setTableRowWeight() {
        return new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 1);
    }

    int getColor(String status) {
        switch (status) {
            case "Added":
                return R.color.colorGreen;
            case "Checked":
            case "UnChecked":
                return R.color.mainColor;
            case "Edited":
                return R.color.colorBlue;
            case "Deleted":
                return R.color.colorRed;
        }
        return R.color.black; // Default color for completed status
    }

}