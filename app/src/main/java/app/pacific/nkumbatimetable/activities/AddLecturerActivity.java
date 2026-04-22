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
import app.pacific.nkumbatimetable.models.Lecturer;

public class AddLecturerActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etSpecialization;
    private AutoCompleteTextView actvDepartment;
    private MaterialButton btnSave;
    private DatabaseHelper dbHelper;
    private int lecturerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecturer);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etLecturerName);
        etEmail = findViewById(R.id.etLecturerEmail);
        etPhone = findViewById(R.id.etLecturerPhone);
        actvDepartment = findViewById(R.id.actvLecturerDept);
        etSpecialization = findViewById(R.id.etSpecialization);
        btnSave = findViewById(R.id.btnSaveLecturer);

        String[] departments = getResources().getStringArray(R.array.departments);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, departments);
        actvDepartment.setAdapter(deptAdapter);

        if (getIntent().hasExtra("lecturer_id")) {
            lecturerId = getIntent().getIntExtra("lecturer_id", -1);
            toolbar.setTitle(R.string.edit_lecturer);
            btnSave.setText(R.string.update);

            etName.setText(getIntent().getStringExtra("lecturer_name"));
            etEmail.setText(getIntent().getStringExtra("lecturer_email"));
            etPhone.setText(getIntent().getStringExtra("lecturer_phone"));
            actvDepartment.setText(getIntent().getStringExtra("lecturer_department"), false);
            etSpecialization.setText(getIntent().getStringExtra("lecturer_specialization"));
        }

        btnSave.setOnClickListener(v -> saveLecturer());
    }

    private void saveLecturer() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String department = actvDepartment.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Lecturer name is required");
            etName.requestFocus();
            return;
        }

        Lecturer lecturer = new Lecturer();
        lecturer.setName(name);
        lecturer.setEmail(email);
        lecturer.setPhone(phone);
        lecturer.setDepartment(department);
        lecturer.setSpecialization(specialization);

        if (lecturerId != -1) {
            lecturer.setId(lecturerId);
            int result = dbHelper.updateLecturer(lecturer);
            if (result > 0) {
                Toast.makeText(this, "Lecturer updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update lecturer", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = dbHelper.insertLecturer(lecturer);
            if (result != -1) {
                Toast.makeText(this, "Lecturer added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add lecturer", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
