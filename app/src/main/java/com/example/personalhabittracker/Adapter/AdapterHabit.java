package com.example.personalhabittracker.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalhabittracker.Model.Habit;
import com.example.personalhabittracker.R;
import com.example.personalhabittracker.databinding.ItemHabitBinding;
import com.example.personalhabittracker.utils.ManageHabits;

import java.util.ArrayList;

public class AdapterHabit extends RecyclerView.Adapter<AdapterHabit.ViewHolder> {

    ManageHabits manageHabits = new ManageHabits();
    Context context;
    ArrayList<Habit> mHabits;
    private OnItemClickListener mListener;

    public AdapterHabit(Context context, ArrayList<Habit> habits, OnItemClickListener mListener) {
        this.context = context;
        this.mHabits = habits;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemHabitBinding binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = mHabits.get(position);
        holder.binding.completed.setOnCheckedChangeListener(null);

        holder.binding.repeat.setText(habit.getRepeat());

        holder.binding.title.setText(habit.getTitle());
        holder.binding.description.setText(habit.getDescription());
        holder.binding.completed.setChecked(habit.isCompleted());
        holder.binding.time.setText(habit.getTime());

        holder.binding.completed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setCompleted(isChecked);
            manageHabits.saveHabit(habit, context, context.getString((isChecked ? R.string.checked_successfully : R.string.un_checked_successfully)), (isChecked ? "Checked" : "UnChecked"));
        });

        holder.binding.delete.setOnClickListener(v -> {
            manageHabits.showDialogDeleteMsgLayout(habit, context);
        });
    }

    @Override
    public int getItemCount() {
        return mHabits.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHabitBinding binding;

        public ViewHolder(ItemHabitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Click(position);
                }
            }
        }
    }
}
