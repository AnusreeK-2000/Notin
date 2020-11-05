package com.example.notin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.entities.Courses;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesHolder>  {
    ArrayList<Courses> coursesList;

    public CoursesAdapter(ArrayList<Courses> coursesList) {
        this.coursesList = coursesList;
    }



    @NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_card_design, parent, false);
        CoursesHolder coursesViewHolder = new CoursesHolder(view);
        return coursesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesHolder holder, int position) {
        Courses helperClass = coursesList.get(position);

        holder.textView.setText(helperClass.getTitle());
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public static class CoursesHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public CoursesHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.course_title);
        }
    }

}
