package app.pacific.nkumbatimetable.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app.pacific.nkumbatimetable.R;
import app.pacific.nkumbatimetable.database.DatabaseHelper;
import app.pacific.nkumbatimetable.models.Course;
import app.pacific.nkumbatimetable.models.Lecturer;
import app.pacific.nkumbatimetable.models.Schedule;

public class AddScheduleActivity extends AppCompatActivity {

    private AutoCompleteTextView actvCourse, actvLecturer, actvDay;
    private TextInputEditText etStartTime, etEndTime, etRoom;
    private MaterialButton btnSave;
    private DatabaseHelper dbHelper;
    private int scheduleId = -1;

    private List<Course> courseList;
    private List<Lecturer> lecturerList;
    private int selectedCourseId = -1;
    private int selectedLecturerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        actvCourse = findViewById(R.id.actvCourse);
        actvLecturer = findViewById(R.id.actvLecturer);
        actvDay = findViewById(R.id.actvDay);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etRoom = findViewById(R.id.etRoom);
        btnSave = findViewById(R.id.btnSaveSchedule);

        setupDropdowns();
        setupTimePickers();

        if (getIntent().hasExtra("schedule_id")) {
            scheduleId = getIntent().getIntExtra("schedule_id", -1);
            toolbar.setTitle(R.string.edit_schedule);
            btnSave.setText(R.string.update);
            populateFields();
        }

        btnSave.setOnClickListener(v -> saveSchedule());
    }

    private void setupDropdowns() {
        courseList = dbHelper.getAllCourses();
        String[] courseNames = new String[courseList.size()];
        for (int i = 0; i < courseList.size(); i++) {
            courseNames[i] = courseList.get(i).toString();
        }
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, courseNames);
        actvCourse.setAdapter(courseAdapter);
        actvCourse.setOnItemClickListener((parent, view, position, id) ->
                selectedCourseId = courseList.get(position).getId());

        lecturerList = dbHelper.getAllLecturers();
        String[] lecturerNames = new String[lecturerList.size()];
        for (int i = 0; i < lecturerList.size(); i++) {
            lecturerNames[i] = lecturerList.get(i).toString();
        }
        ArrayAdapter<String> lecturerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, lecturerNames);
        actvLecturer.setAdapter(lecturerAdapter);
        actvLecturer.setOnItemClickListener((parent, view, position, id) ->
                selectedLecturerId = lecturerList.get(position).getId());

        String[] days = getResources().getStringArray(R.array.days_of_week);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, days);
        actvDay.setAdapter(dayAdapter);
    }

    private void setupTimePickers() {
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
    }

    private void showTimePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            editText.setText(time);
        }, hour, minute, true);
        dialog.show();
    }

    private void populateFields() {
        int courseId = getIntent().getIntExtra("course_id", -1);
        int lecturerId = getIntent().getIntExtra("lecturer_id", -1);

        selectedCourseId = courseId;
        selectedLecturerId = lecturerId;

        for (Course course : courseList) {
            if (course.getId() == courseId) {
                actvCourse.setText(course.toString(), false);
                break;
            }
        }

        for (Lecturer lecturer : lecturerList) {
            if (lecturer.getId() == lecturerId) {
                actvLecturer.setText(lecturer.toString(), false);
                break;
            }
        }

        actvDay.setText(getIntent().getStringExtra("day_of_week"), false);
        etStartTime.setText(getIntent().getStringExtra("start_time"));
        etEndTime.setText(getIntent().getStringExtra("end_time"));
        etRoom.setText(getIntent().getStringExtra("room"));
    }

    private void saveSchedule() {
        String day = actvDay.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String room = etRoom.getText().toString().trim();

        if (selectedCourseId == -1) {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLecturerId == -1) {
            Toast.makeText(this, "Please select a lecturer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (day.isEmpty()) {
            Toast.makeText(this, "Please select a day", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime.isEmpty()) {
            Toast.makeText(this, "Please set start time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime.isEmpty()) {
            Toast.makeText(this, "Please set end time", Toast.LENGTH_SHORT).show();
            return;
        }

        Schedule schedule = new Schedule();
        schedule.setCourseId(selectedCourseId);
        schedule.setLecturerId(selectedLecturerId);
        schedule.setDayOfWeek(day);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setRoom(room);

        if (scheduleId != -1) {
            schedule.setId(scheduleId);
            int result = dbHelper.updateSchedule(schedule);
            if (result > 0) {
                Toast.makeText(this, "Schedule updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update schedule", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = dbHelper.insertSchedule(schedule);
            if (result != -1) {
                Toast.makeText(this, "Schedule added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add schedule", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
