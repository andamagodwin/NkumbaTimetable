package app.pacific.nkumbatimetable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.adapters.ScheduleAdapter;
import app.pacific.nkumbatimetable.database.DatabaseHelper;
import app.pacific.nkumbatimetable.models.Schedule;

public class ScheduleListActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionListener {

    private RecyclerView rvSchedules;
    private TextView tvNoSchedules;
    private FloatingActionButton fabAddSchedule;
    private ScheduleAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        rvSchedules = findViewById(R.id.rvSchedules);
        tvNoSchedules = findViewById(R.id.tvNoSchedules);
        fabAddSchedule = findViewById(R.id.fabAddSchedule);

        rvSchedules.setLayoutManager(new LinearLayoutManager(this));

        fabAddSchedule.setOnClickListener(v -> {
            if (dbHelper.getCoursesCount() == 0 || dbHelper.getLecturersCount() == 0) {
                Toast.makeText(this,
                        "Please add at least one course and one lecturer first",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, AddScheduleActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }

    private void loadSchedules() {
        List<Schedule> schedules = dbHelper.getAllSchedules();
        if (schedules.isEmpty()) {
            tvNoSchedules.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
        } else {
            tvNoSchedules.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new ScheduleAdapter(schedules, this);
                rvSchedules.setAdapter(adapter);
            } else {
                adapter.updateList(schedules);
            }
        }
    }

    @Override
    public void onEditSchedule(Schedule schedule) {
        Intent intent = new Intent(this, AddScheduleActivity.class);
        intent.putExtra("schedule_id", schedule.getId());
        intent.putExtra("course_id", schedule.getCourseId());
        intent.putExtra("lecturer_id", schedule.getLecturerId());
        intent.putExtra("day_of_week", schedule.getDayOfWeek());
        intent.putExtra("start_time", schedule.getStartTime());
        intent.putExtra("end_time", schedule.getEndTime());
        intent.putExtra("room", schedule.getRoom());
        startActivity(intent);
    }

    @Override
    public void onDeleteSchedule(Schedule schedule) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage("Delete this schedule entry?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dbHelper.deleteSchedule(schedule.getId());
                    loadSchedules();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
