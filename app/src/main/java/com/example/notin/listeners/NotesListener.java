package com.example.notin.listeners;

import com.example.notin.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
