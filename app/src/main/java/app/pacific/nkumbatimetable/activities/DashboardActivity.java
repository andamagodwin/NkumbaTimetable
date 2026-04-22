package app.pacific.nkumbatimetable.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.database.DatabaseHelper;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvCourseCount, tvLecturerCount, tvScheduleCount;
    private MaterialCardView cardCourses, cardLecturers, cardSchedule, cardTimetable;
    private MaterialButton btnLogout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvCourseCount = findViewById(R.id.tvCourseCount);
        tvLecturerCount = findViewById(R.id.tvLecturerCount);
        tvScheduleCount = findViewById(R.id.tvScheduleCount);
        cardCourses = findViewById(R.id.cardCourses);
        cardLecturers = findViewById(R.id.cardLecturers);
        cardSchedule = findViewById(R.id.cardSchedule);
        cardTimetable = findViewById(R.id.cardTimetable);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences("NkumbaTimetable", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "User");
        tvWelcome.setText("Welcome, " + fullName);

        cardCourses.setOnClickListener(v ->
                startActivity(new Intent(this, CourseListActivity.class)));

        cardLecturers.setOnClickListener(v ->
                startActivity(new Intent(this, LecturerListActivity.class)));

        cardSchedule.setOnClickListener(v ->
                startActivity(new Intent(this, ScheduleListActivity.class)));

        cardTimetable.setOnClickListener(v ->
                startActivity(new Intent(this, TimetableActivity.class)));

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvCourseCount.setText(String.valueOf(dbHelper.getCoursesCount()));
        tvLecturerCount.setText(String.valueOf(dbHelper.getLecturersCount()));
        tvScheduleCount.setText(String.valueOf(dbHelper.getSchedulesCount()));
    }
}
