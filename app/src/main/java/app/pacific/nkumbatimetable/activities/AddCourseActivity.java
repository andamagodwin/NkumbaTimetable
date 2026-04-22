package app.pacific.nkumbatimetable.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.database.DatabaseHelper;
import app.pacific.nkumbatimetable.models.Course;

public class AddCourseActivity extends AppCompatActivity {

    private TextInputEditText etCourseCode, etCourseName, etCreditHours, etDescription;
    private AutoCompleteTextView actvDepartment;
    private MaterialButton btnSave;
    private DatabaseHelper dbHelper;
    private int courseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        etCourseCode = findViewById(R.id.etCourseCode);
        etCourseName = findViewById(R.id.etCourseName);
        etCreditHours = findViewById(R.id.etCreditHours);
        actvDepartment = findViewById(R.id.actvDepartment);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSaveCourse);

        String[] departments = getResources().getStringArray(R.array.departments);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, departments);
        actvDepartment.setAdapter(deptAdapter);

        if (getIntent().hasExtra("course_id")) {
            courseId = getIntent().getIntExtra("course_id", -1);
            toolbar.setTitle(R.string.edit_course);
            btnSave.setText(R.string.update);

            etCourseCode.setText(getIntent().getStringExtra("course_code"));
            etCourseName.setText(getIntent().getStringExtra("course_name"));
            etCreditHours.setText(String.valueOf(getIntent().getIntExtra("credit_hours", 3)));
            actvDepartment.setText(getIntent().getStringExtra("department"), false);
            etDescription.setText(getIntent().getStringExtra("description"));
        }

        btnSave.setOnClickListener(v -> saveCourse());
    }

    private void saveCourse() {
        String code = etCourseCode.getText().toString().trim();
        String name = etCourseName.getText().toString().trim();
        String creditStr = etCreditHours.getText().toString().trim();
        String department = actvDepartment.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (code.isEmpty()) {
            etCourseCode.setError("Course code is required");
            etCourseCode.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            etCourseName.setError("Course name is required");
            etCourseName.requestFocus();
            return;
        }

        int creditHours = 3;
        if (!creditStr.isEmpty()) {
            creditHours = Integer.parseInt(creditStr);
        }

        Course course = new Course();
        course.setCourseCode(code.toUpperCase());
        course.setCourseName(name);
        course.setCreditHours(creditHours);
        course.setDepartment(department);
        course.setDescription(description);

        if (courseId != -1) {
            course.setId(courseId);
            int result = dbHelper.updateCourse(course);
            if (result > 0) {
                Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update course", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = dbHelper.insertCourse(course);
            if (result != -1) {
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add course. Code may already exist.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
