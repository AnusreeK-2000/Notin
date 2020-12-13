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
import com.example.notin.Student.UploadPDFDetails;
import com.example.notin.Student.pdfViewerActivity;
import com.example.notin.entities.RecentNotes;

import java.util.ArrayList;

public class RecentNotesAdapter extends RecyclerView.Adapter<RecentNotesAdapter.RecentNotesHolder>  {
    ArrayList<UploadPDFDetails> recentNotesList;

    public RecentNotesAdapter(ArrayList<UploadPDFDetails> recentNotesList) {
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
    public void onBindViewHolder(@NonNull RecentNotesHolder holder, final int position) {
        final UploadPDFDetails helperClass = recentNotesList.get(position);

        holder.textView.setText(helperClass.getName());

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent=new Intent(context, pdfViewerActivity.class);

                //sending position of the listdata to retrieve the path and open new activity
                //intent.putExtra("position",position);
                intent.putExtra("title",recentNotesList.get(position).getName());
                intent.putExtra("link",recentNotesList.get(position).getUrl());
                context.startActivity(intent);

            }
        });
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
