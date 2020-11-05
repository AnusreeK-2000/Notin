package com.example.notin.adapters;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<com.example.notin.adapters.NotesAdapter.NotesViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener=notesListener;
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
}
