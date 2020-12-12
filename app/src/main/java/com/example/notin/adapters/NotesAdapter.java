package com.example.notin.adapters;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.entities.Note;
import com.example.notin.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<com.example.notin.adapters.NotesAdapter.NotesViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener=notesListener;
        notesSource=notes;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, final int position) {
    holder.setNote((notes.get(position)));
    holder.layoutNote.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            notesListener.onNoteClicked(notes.get(position),position);
        }
    });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle,textDesc,textDateTime;
        LinearLayout layoutNote;

       NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.textTitle);
            textDesc=itemView.findViewById(R.id.textDesc);
            textDateTime=itemView.findViewById(R.id.textDateTime);
            layoutNote=itemView.findViewById(R.id.layoutNote);
        }
        void setNote(Note note){
           textTitle.setText(note.getTitle());
           textDesc.setText(note.getNoteText());
           textDateTime.setText(note.getDateTime());
            GradientDrawable gradientDrawable=(GradientDrawable) layoutNote.getBackground();
            if(note.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }
            else{
                gradientDrawable.setColor(Color.parseColor("#ffffff"));
            }


        }
    }
    public void searchNotes(final String searchKeyword){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                  notes=notesSource;
                }
                else{
                    ArrayList<Note>temp=new ArrayList<>();
                    for(Note note:notesSource ){
                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                       || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    notes=temp;
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
