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
import app.pacific.nkumbatimetable.models.Course;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onEditCourse(Course course);
        void onDeleteCourse(Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseActionListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvCourseCode.setText(course.getCourseCode());
        holder.tvCourseName.setText(course.getCourseName());
        holder.tvDepartment.setText(course.getDepartment());
        holder.tvCreditHours.setText(course.getCreditHours() + " Credit Hours");

        holder.btnEdit.setOnClickListener(v -> listener.onEditCourse(course));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteCourse(course));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void updateList(List<Course> newList) {
        this.courseList = newList;
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvCourseName, tvDepartment, tvCreditHours;
        ImageButton btnEdit, btnDelete;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvCreditHours = itemView.findViewById(R.id.tvCreditHours);
            btnEdit = itemView.findViewById(R.id.btnEditCourse);
            btnDelete = itemView.findViewById(R.id.btnDeleteCourse);
        }
    }
}
