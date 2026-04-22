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
import app.pacific.nkumbatimetable.adapters.LecturerAdapter;
import app.pacific.nkumbatimetable.database.DatabaseHelper;
import app.pacific.nkumbatimetable.models.Lecturer;

public class LecturerListActivity extends AppCompatActivity implements LecturerAdapter.OnLecturerActionListener {

    private RecyclerView rvLecturers;
    private TextView tvNoLecturers;
    private FloatingActionButton fabAddLecturer;
    private LecturerAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);

        rvLecturers = findViewById(R.id.rvLecturers);
        tvNoLecturers = findViewById(R.id.tvNoLecturers);
        fabAddLecturer = findViewById(R.id.fabAddLecturer);

        rvLecturers.setLayoutManager(new LinearLayoutManager(this));

        fabAddLecturer.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLecturerActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLecturers();
    }

    private void loadLecturers() {
        List<Lecturer> lecturers = dbHelper.getAllLecturers();
        if (lecturers.isEmpty()) {
            tvNoLecturers.setVisibility(View.VISIBLE);
            rvLecturers.setVisibility(View.GONE);
        } else {
            tvNoLecturers.setVisibility(View.GONE);
            rvLecturers.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new LecturerAdapter(lecturers, this);
                rvLecturers.setAdapter(adapter);
            } else {
                adapter.updateList(lecturers);
            }
        }
    }

    @Override
    public void onEditLecturer(Lecturer lecturer) {
        Intent intent = new Intent(this, AddLecturerActivity.class);
        intent.putExtra("lecturer_id", lecturer.getId());
        intent.putExtra("lecturer_name", lecturer.getName());
        intent.putExtra("lecturer_email", lecturer.getEmail());
        intent.putExtra("lecturer_phone", lecturer.getPhone());
        intent.putExtra("lecturer_department", lecturer.getDepartment());
        intent.putExtra("lecturer_specialization", lecturer.getSpecialization());
        startActivity(intent);
    }

    @Override
    public void onDeleteLecturer(Lecturer lecturer) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage("Delete lecturer " + lecturer.getName() + "?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dbHelper.deleteLecturer(lecturer.getId());
                    loadLecturers();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
