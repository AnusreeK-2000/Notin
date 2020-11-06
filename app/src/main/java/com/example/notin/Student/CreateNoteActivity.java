package com.example.notin.Student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notin.R;
import com.example.notin.database.NotesDatabase;
import com.example.notin.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteText;
    private TextView textDateTime;

    private Note alreadyAvailableNote;

    //Add camera btn
    ImageView camera;

    SeekBar seekBar;
    TextView txtSeekBar;
    int textSize = 1;
    int saveProgress;

    private View viewTitle;
    private String selectedColor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //back button
        ImageView imageBack = findViewById(R.id.ImageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                startActivity(new Intent(CreateNoteActivity.this,MainActivity.class));
            }
        });

        inputNoteTitle = findViewById(R.id.Create_Title);
        inputNoteText = findViewById(R.id.description_text);
        textDateTime = findViewById(R.id.DateTime);
        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );


        camera = findViewById(R.id.camera_btn);
        Button save = findViewById(R.id.Save_btn);


        viewTitle = findViewById(R.id.NoteColorIndicator);
        //For seek bar
        txtSeekBar = (TextView) findViewById(R.id.description_text);
        txtSeekBar.setTextScaleX(textSize);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSize = textSize + (progress - saveProgress);
                saveProgress = progress;
                txtSeekBar.setTextSize(textSize);
            }
        });


        //INITIAL BUTTON FUNCTION
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent click=new Intent();
                    click.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(click);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //IT CLEARS TEXT FOR NOW ON CLICKING SAVE
                /*
                TextView desctext = findViewById(R.id.description_text);
                TextView Title=findViewById(R.id.Create_Title);

                Title.setText("");
                desctext.setText("");
                */
                saveNote();
            }
        });


        selectedColor = "#ffffff";

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        initMiscellaneous();
        setTitleIndicatorColor();
    }


    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());

    }

    private void saveNote() {
        //validations
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        //saving to db
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedColor);

        //getting id of new note from already available note,as OnConflictStrategy is set to "replace" in noteDao,hence it will be updated
        if (alreadyAvailableNote != null) {
            note.setId(alreadyAvailableNote.getId());
        }

        //use async task to save in room db

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveNoteTask().execute();
    }

    public void initMiscellaneous() {
        final LinearLayout layoutMisc = findViewById(R.id.layout_Miscellaneous);
        final BottomSheetBehavior BottomSheet = BottomSheetBehavior.from(layoutMisc);
        layoutMisc.findViewById(R.id.MiscText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BottomSheet.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    BottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    BottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        //TO change color of note
        final ImageView imageColor1 = layoutMisc.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMisc.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMisc.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMisc.findViewById(R.id.imageColor4);

        layoutMisc.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#808080";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                setTitleIndicatorColor();
            }
        });

        layoutMisc.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#3a52fc";
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor1.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                setTitleIndicatorColor();
            }
        });
        layoutMisc.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#fd8e38";
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor4.setImageResource(0);
                setTitleIndicatorColor();
            }
        });
        layoutMisc.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#ff4842";
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor1.setImageResource(0);
                setTitleIndicatorColor();
            }
        });

        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()) {
            switch (alreadyAvailableNote.getColor()) {
                case "#808080":
                    layoutMisc.findViewById(R.id.viewColor1).performClick();
                    break;
                case "#3a52fc":
                    layoutMisc.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#fd8e38":
                    layoutMisc.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#ff4842":
                    layoutMisc.findViewById(R.id.viewColor4).performClick();

            }
        }

    }

    //Function to change note color
    private void setTitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewTitle.getBackground();
        gradientDrawable.setColor((Color.parseColor(selectedColor)));
    }
}

