package com.example.notin.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.Teacher.ShowVideo;
import com.example.notin.entities.Courses;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VideoCoursesAdapter extends RecyclerView.Adapter<VideoCoursesAdapter.CoursesHolder>  {
    ArrayList<Courses> coursesList;
    private Timer timer;
    ArrayList<Courses>list2;

    public VideoCoursesAdapter(ArrayList<Courses> coursesList) {

        this.coursesList = coursesList;
        list2=coursesList;
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
    public void searchNotes(final String searchKeyword){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    coursesList=list2;
                }
                else{
                    ArrayList<Courses>temp=new ArrayList<>();
                    for(Courses note:list2){
                        if(note.getName().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    coursesList=temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        },500);
    }

    public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }




}

