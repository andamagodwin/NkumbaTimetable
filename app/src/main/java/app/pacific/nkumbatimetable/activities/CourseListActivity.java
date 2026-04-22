package app.pacific.nkumbatimetable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.adapters.CourseAdapter;
import app.pacific.nkumbatimetable.database.DatabaseHelper;
import app.pacific.nkumbatimetable.models.Course;

public class CourseListActivity extends AppCompatActivity implements CourseAdapter.OnCourseActionListener {

    private RecyclerView rvCourses;
    private TextView tvNoCourses;
    private FloatingActionButton fabAddCourse;
    private CourseAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        rvCourses = findViewById(R.id.rvCourses);
        tvNoCourses = findViewById(R.id.tvNoCourses);
        fabAddCourse = findViewById(R.id.fabAddCourse);

        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCourseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    private void loadCourses() {
        List<Course> courses = dbHelper.getAllCourses();
        if (courses.isEmpty()) {
            tvNoCourses.setVisibility(View.VISIBLE);
            rvCourses.setVisibility(View.GONE);
        } else {
            tvNoCourses.setVisibility(View.GONE);
            rvCourses.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new CourseAdapter(courses, this);
                rvCourses.setAdapter(adapter);
            } else {
                adapter.updateList(courses);
            }
        }
    }

    @Override
    public void onEditCourse(Course course) {
        Intent intent = new Intent(this, AddCourseActivity.class);
        intent.putExtra("course_id", course.getId());
        intent.putExtra("course_code", course.getCourseCode());
        intent.putExtra("course_name", course.getCourseName());
        intent.putExtra("credit_hours", course.getCreditHours());
        intent.putExtra("department", course.getDepartment());
        intent.putExtra("description", course.getDescription());
        startActivity(intent);
    }

    @Override
    public void onDeleteCourse(Course course) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage("Delete course " + course.getCourseCode() + " - " + course.getCourseName() + "?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dbHelper.deleteCourse(course.getId());
                    loadCourses();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
