package app.pacific.nkumbatimetable.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.models.Schedule;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private OnScheduleActionListener listener;

    public interface OnScheduleActionListener {
        void onEditSchedule(Schedule schedule);
        void onDeleteSchedule(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> scheduleList, OnScheduleActionListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.tvDay.setText(schedule.getDayOfWeek());
        holder.tvTime.setText(schedule.getStartTime() + "\n" + schedule.getEndTime());
        holder.tvCourseCode.setText(schedule.getCourseCode());
        holder.tvCourseName.setText(schedule.getCourseName());
        holder.tvLecturer.setText(schedule.getLecturerName());
        holder.tvRoom.setText("Room: " + schedule.getRoom());

        holder.btnEdit.setOnClickListener(v -> listener.onEditSchedule(schedule));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteSchedule(schedule));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void updateList(List<Schedule> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime, tvCourseCode, tvCourseName, tvLecturer, tvRoom;
        ImageButton btnEdit, btnDelete;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvScheduleDay);
            tvTime = itemView.findViewById(R.id.tvScheduleTime);
            tvCourseCode = itemView.findViewById(R.id.tvScheduleCourseCode);
            tvCourseName = itemView.findViewById(R.id.tvScheduleCourseName);
            tvLecturer = itemView.findViewById(R.id.tvScheduleLecturer);
            tvRoom = itemView.findViewById(R.id.tvScheduleRoom);
            btnEdit = itemView.findViewById(R.id.btnEditSchedule);
            btnDelete = itemView.findViewById(R.id.btnDeleteSchedule);
        }
    }
}
