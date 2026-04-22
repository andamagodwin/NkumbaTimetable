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
import app.pacific.nkumbatimetable.models.Lecturer;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder> {

    private List<Lecturer> lecturerList;
    private OnLecturerActionListener listener;

    public interface OnLecturerActionListener {
        void onEditLecturer(Lecturer lecturer);
        void onDeleteLecturer(Lecturer lecturer);
    }

    public LecturerAdapter(List<Lecturer> lecturerList, OnLecturerActionListener listener) {
        this.lecturerList = lecturerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LecturerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lecturer, parent, false);
        return new LecturerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LecturerViewHolder holder, int position) {
        Lecturer lecturer = lecturerList.get(position);
        holder.tvName.setText(lecturer.getName());
        holder.tvDepartment.setText(lecturer.getDepartment());
        holder.tvEmail.setText(lecturer.getEmail());
        holder.tvPhone.setText(lecturer.getPhone());

        String initial = lecturer.getName() != null && !lecturer.getName().isEmpty()
                ? lecturer.getName().substring(0, 1).toUpperCase() : "?";
        holder.tvInitial.setText(initial);

        holder.btnEdit.setOnClickListener(v -> listener.onEditLecturer(lecturer));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteLecturer(lecturer));
    }

    @Override
    public int getItemCount() {
        return lecturerList.size();
    }

    public void updateList(List<Lecturer> newList) {
        this.lecturerList = newList;
        notifyDataSetChanged();
    }

    static class LecturerViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvDepartment, tvEmail, tvPhone;
        ImageButton btnEdit, btnDelete;

        LecturerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvLecturerInitial);
            tvName = itemView.findViewById(R.id.tvLecturerName);
            tvDepartment = itemView.findViewById(R.id.tvLecturerDepartment);
            tvEmail = itemView.findViewById(R.id.tvLecturerEmail);
            tvPhone = itemView.findViewById(R.id.tvLecturerPhone);
            btnEdit = itemView.findViewById(R.id.btnEditLecturer);
            btnDelete = itemView.findViewById(R.id.btnDeleteLecturer);
        }
    }
}
