package app.pacific.nkumbatimetable.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.adapters.ScheduleAdapter;
import app.pacific.nkumbatimetable.database.DatabaseHelper;
import app.pacific.nkumbatimetable.models.Schedule;

public class TimetableActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionListener {

    private TabLayout tabLayout;
    private RecyclerView rvTimetable;
    private TextView tvNoClasses;
    private DatabaseHelper dbHelper;
    private ScheduleAdapter adapter;

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        tabLayout = findViewById(R.id.tabLayout);
        rvTimetable = findViewById(R.id.rvTimetable);
        tvNoClasses = findViewById(R.id.tvNoClasses);

        rvTimetable.setLayoutManager(new LinearLayoutManager(this));

        for (String day : days) {
            tabLayout.addTab(tabLayout.newTab().setText(day));
        }

        int todayIndex = getTodayIndex();
        if (todayIndex >= 0 && todayIndex < days.length) {
            TabLayout.Tab tab = tabLayout.getTabAt(todayIndex);
            if (tab != null) tab.select();
            loadSchedulesForDay(days[todayIndex]);
        } else {
            loadSchedulesForDay(days[0]);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadSchedulesForDay(days[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private int getTodayIndex() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY: return 0;
            case Calendar.TUESDAY: return 1;
            case Calendar.WEDNESDAY: return 2;
            case Calendar.THURSDAY: return 3;
            case Calendar.FRIDAY: return 4;
            case Calendar.SATURDAY: return 5;
            default: return 0;
        }
    }

    private void loadSchedulesForDay(String day) {
        List<Schedule> schedules = dbHelper.getSchedulesByDay(day);
        if (schedules.isEmpty()) {
            tvNoClasses.setVisibility(View.VISIBLE);
            rvTimetable.setVisibility(View.GONE);
        } else {
            tvNoClasses.setVisibility(View.GONE);
            rvTimetable.setVisibility(View.VISIBLE);
            adapter = new ScheduleAdapter(schedules, this);
            rvTimetable.setAdapter(adapter);
        }
    }

    @Override
    public void onEditSchedule(Schedule schedule) {}

    @Override
    public void onDeleteSchedule(Schedule schedule) {}
}
