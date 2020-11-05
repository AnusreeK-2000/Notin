package com.example.notin.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notin.dao.NoteDao;
import com.example.notin.entities.Note;


@Database(entities = {Note.class},version = 1,exportSchema = false )
public abstract class NotesDatabase extends RoomDatabase {

    private static com.example.notin.database.NotesDatabase notesDatabase;


    public static synchronized com.example.notin.database.NotesDatabase getDatabase(Context context){
        if(notesDatabase==null){

            notesDatabase= Room.databaseBuilder(
                    context,
                    com.example.notin.database.NotesDatabase.class, "notes_db").build();
        }
        return notesDatabase;
    }
    public abstract NoteDao noteDao();
}
