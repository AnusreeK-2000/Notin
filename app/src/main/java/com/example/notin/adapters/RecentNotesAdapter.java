package com.example.notin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.entities.RecentNotes;

import java.util.ArrayList;

public class RecentNotesAdapter extends RecyclerView.Adapter<RecentNotesAdapter.RecentNotesHolder>  {
    ArrayList<RecentNotes> recentNotesList;

    public RecentNotesAdapter(ArrayList<RecentNotes> recentNotesList) {
        this.recentNotesList = recentNotesList;
    }



    @NonNull
    @Override
    public RecentNotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_notes_card_design, parent, false);
        RecentNotesHolder recentNotesViewHolder = new RecentNotesHolder(view);
        return recentNotesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentNotesHolder holder, int position) {
        RecentNotes helperClass = recentNotesList.get(position);

        holder.textView.setText(helperClass.getTitle());
    }

    @Override
    public int getItemCount() {
        return recentNotesList.size();
    }

    public static class RecentNotesHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public RecentNotesHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.note_title);
        }
    }

}
