package com.example.personalhabittracker.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.personalhabittracker.Adapter.AdapterHabit;
import com.example.personalhabittracker.Model.Habit;
import com.example.personalhabittracker.databinding.FragmentHomeBinding;
import com.example.personalhabittracker.utils.ManageHabits;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements AdapterHabit.OnItemClickListener {

    FragmentHomeBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHabits = database.getReference("Habits");

    ManageHabits manageHabits = new ManageHabits();

    ArrayList<Habit> mHabits;
    AdapterHabit adapterHabit;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        init();
        getAllHabits();

        return binding.getRoot();
    }

    void init() {
        mHabits = new ArrayList<>();
        adapterHabit = new AdapterHabit(getContext(), mHabits, this);
        binding.habitsRecyclerView.setAdapter(adapterHabit);
    }

    void getAllHabits() {
        binding.progressBar.setVisibility(View.VISIBLE);
        refHabits.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHabits.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Habit habit = snapshot.getValue(Habit.class);
                    if (habit != null) {
                        if (!habit.isCompleted()) mHabits.add(habit);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
                adapterHabit.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItem_Click(int position) {
        Habit habit = mHabits.get(position);
        manageHabits.showDialogAddHabitLayout(habit, getActivity());
    }
}