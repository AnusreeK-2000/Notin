
package com.example.notin.Student;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.notin.BuildConfig;
import com.example.notin.R;
import com.example.notin.database.NotesDatabase;
import com.example.notin.entities.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MASK;
import static android.view.MotionEvent.ACTION_UP;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteText;
    private TextView textDateTime;

    private Note alreadyAvailableNote;
    private AlertDialog dialogDelete;
    private AlertDialog DeleteImageDialog;

    //Add camera btn
    ImageView camera;

    //Seek bar for text size
    SeekBar seekBar;
    TextView txtSeekBar;
    int textSize = 1;
    int saveProgress;

    //For camera
    static final int REQUEST_IMAGE_CAPTURE = 100;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;

    //For microphone
    final private int MICROPHONE_PERMISSIONS = 102;

    int i = 1;
    private View viewTitle;
    private String selectedColor;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSIONS);
            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       /* if(requestCode==CAMERA_PERMISSION && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                try {
                    dispatchTakePictureIntent();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(CreateNoteActivity.this,"Camera Permission not provided",Toast.LENGTH_SHORT).show();
            }*/

        switch (requestCode) {

            //If neither permission given
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalfile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermission && writeExternalfile) {
                        try {
                            dispatchTakePictureIntent();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CreateNoteActivity.this, "Camera and storage Permission not provided", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case MICROPHONE_PERMISSIONS:
                if (requestCode == MICROPHONE_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(CreateNoteActivity.this, "Microphone Permission not provided", Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        checkPermission();


        //for carousel of images
        // ViewPager viewPager=findViewById(R.id.viewpager);
        //  ImageAdapter adapter2=new ImageAdapter(this);
        //  viewPager.setAdapter(adapter2);

        //back button
        ImageView imageBack = findViewById(R.id.ImageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
            }
        });

        //--scroll desc
        EditText et = findViewById(R.id.description_text);

        et.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.description_text) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & ACTION_MASK) {
                        case ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });


        //back button
        // ImageView imageBack = findViewById(R.id.ImageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
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
        ImageView del = findViewById(R.id.del_btn);

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


        //Camera BUTTON FUNCTION
        camera.setOnClickListener(new View.OnClickListener() {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();

            @Override
            public void onClick(View v) {
                //Both camera and storage not provided
                if (ContextCompat.checkSelfPermission(CreateNoteActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(CreateNoteActivity.this,
                                Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        dispatchTakePictureIntent();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
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

        //Speech to text

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    inputNoteText.setText(inputNoteText.getText() + matches.get(0) + "\n");
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        findViewById(R.id.audio_btn).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        inputNoteText.setHint("You will see input here");
                        break;

                    case ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        //inputNoteText.setText("");
                        inputNoteText.setHint("Listening...");
                        break;
                }
                return false;
            }
        });


        selectedColor = "#808080";

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        if (alreadyAvailableNote != null) {
            del.setVisibility(View.VISIBLE);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();

                }
            });

        }
        initMiscellaneous();
        setTitleIndicatorColor();
    }

    private void showDeleteDialog() {
        if (dialogDelete == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.delete_note,
                    (ViewGroup) findViewById(R.id.deleteNote)
            );
            builder.setView(view);
            dialogDelete = builder.create();
            if (dialogDelete.getWindow() != null) {
                dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();

                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDelete.dismiss();
                }
            });
        }
        dialogDelete.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            displayImage();
        }
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        displayImage();
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
            updateImagePath(inputNoteTitle.getText().toString());
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

    //For when Title is edited
    protected void updateImagePath(String s) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Notin/" + alreadyAvailableNote.getTitle());
        File toDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Notin/" + s);
        storageDir.renameTo(toDir);
    }

    //To display saved photos
    protected void displayImage() {


        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();

        // String targetPath = ExternalStorageDirectoryPath + "/DCIM/App";
        String targetPath = ExternalStorageDirectoryPath + "/DCIM/Notin/" + inputNoteTitle.getText().toString();
        ArrayList<String> images = new ArrayList<String>();
        File targetDirector = new File(targetPath);
        LinearLayout layout = (LinearLayout) findViewById(R.id.imageLayout);
        layout.removeAllViews();
        File[] files = targetDirector.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {

            final String filepath = file.getAbsolutePath();
            Bitmap bmp = BitmapFactory.decodeFile(filepath);
            if (bmp == null) {
                file.delete();
                continue;
            }
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(1000, 1000));
            //To generate view id
            image.setId(View.generateViewId());


            image.setMaxHeight(500);
            image.setMaxWidth(500);
            // Adds the view to the layout
            layout.addView(image);

            image.setImageBitmap(bmp);
            Toast.makeText(this, Integer.toString(image.getId()), Toast.LENGTH_LONG);

            //   ImageView tempImg1,tempImg2,tempImg3,tempImg4,tempImg5,tempImg6,tempImg7,tempImg8,tempImg9,tempImg10;
            final ImageView tempImg1 = (ImageView) findViewById(image.getId());
            final Bitmap bmmpp=((BitmapDrawable)tempImg1.getDrawable()).getBitmap();
            tempImg1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteImageDialog(filepath);
                    Log.i("Onclick", filepath);
                    return true;
                }
            });

            tempImg1.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
//                    Toast.makeText(CreateNoteActivity.this, "Unable to detect text :(", Toast.LENGTH_SHORT).show();
                    detectTextFromImage(bmmpp);
                }
            });

        }
    }

    private void detectTextFromImage(Bitmap bmmpp){
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bmmpp);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateNoteActivity.this, "Unable to detect text :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size() == 0){
            Toast.makeText(CreateNoteActivity.this, "No Text Detected", Toast.LENGTH_SHORT).show();
        }else{
            for(FirebaseVisionText.Block block: firebaseVisionText.getBlocks()){
                String text = block.getText();
                inputNoteText.setText(inputNoteText.getText() + text + "\n");
//                Toast.makeText(CreateNoteActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        }

    }

    //FOR DELETION OF IMAGE
    private void showDeleteImageDialog(final String filepath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.delete_image,
                (ViewGroup) findViewById(R.id.deleteImage)
        );
        builder.setView(view);
        DeleteImageDialog = builder.create();
        if (DeleteImageDialog.getWindow() != null) {
            DeleteImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            view.findViewById(R.id.Imagedelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();

                    String targetPath = ExternalStorageDirectoryPath + "/DCIM/Notin/" + inputNoteTitle.getText().toString();

                    File dir = getFilesDir();
                    File file = new File(filepath);
                    boolean deleted = file.delete();
                    displayImage();
                    DeleteImageDialog.dismiss();
                }
            });


            view.findViewById(R.id.Imagecancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DeleteImageDialog.dismiss();
                }
            });

            DeleteImageDialog.show();
        }
    }


    //Set up the camera intent
    private void dispatchTakePictureIntent() throws InterruptedException {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter a Title pls!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //  startActivityForResult(takePictureIntent, IMAGE_DISPLAY);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Try again!",
                        Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


            }

        }
    }


    //Create Image File
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Notin/" + inputNoteTitle.getText().toString());
        if (!storageDir.exists()) {

            boolean s = new File(storageDir.getPath()).mkdirs();

            if (!s) {
                Toast.makeText(this, "Please give storage permission!",
                        Toast.LENGTH_SHORT).show();
                Log.v("not", "not created");
            } else {
                Log.v("cr", "directory created");
            }
        } else {
            Log.v("directory", "directory exists");
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        Uri uriOfImage = Uri.parse(image.getPath());
        return image;

    }
}

