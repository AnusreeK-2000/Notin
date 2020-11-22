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
    private CourseClickListener mCourseListener;

    public CoursesAdapter(ArrayList<Courses> coursesList, CourseClickListener courseClickListener) {
        this.coursesList = coursesList;
        this.mCourseListener = courseClickListener;
    }




    @NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_card_design, parent, false);
        CoursesHolder coursesViewHolder = new CoursesHolder(view, mCourseListener);
        return coursesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesHolder holder, int position) {
        Courses helperClass = coursesList.get(position);

        holder.textView.setText(helperClass.getName());
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public static class CoursesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        CourseClickListener courseClickListener;

        public CoursesHolder(@NonNull View itemView, CourseClickListener courseClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.course_title);
            this.courseClickListener = courseClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            courseClickListener.onCourseClick(getAdapterPosition());

        }
    }

    public interface CourseClickListener{
        void onCourseClick(int position);
    }

}
