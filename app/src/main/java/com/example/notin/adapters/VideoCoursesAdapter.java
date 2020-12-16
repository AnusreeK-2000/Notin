package com.example.notin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.Student.UploadNotesActivity;
import com.example.notin.Teacher.ShowVideo;
import com.example.notin.entities.Courses;

import java.util.ArrayList;

public class VideoCoursesAdapter extends RecyclerView.Adapter<VideoCoursesAdapter.CoursesHolder>  {
    ArrayList<Courses> coursesList;

    public VideoCoursesAdapter(ArrayList<Courses> coursesList) {
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
        final Courses helperClass = coursesList.get(position);

        holder.textView.setText(helperClass.getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ShowVideo.class);
                intent.putExtra("name",helperClass.getName());
                context.startActivity(intent);

            }
        });
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

