package com.example.notin.Teacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notin.Common.LoginActivity;
import com.example.notin.R;
import com.example.notin.Student.CreateNoteActivity;
import com.example.notin.Student.Home;
import com.example.notin.Student.MainActivity;
import com.example.notin.Student.UpdateProfile;
import com.example.notin.Student.UploadActivity;
import com.example.notin.Utils.SharedPrefUtil;
import com.example.notin.entities.UploadVideoDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class LectureUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_VIDEO = 1;
    private Button Upload;

    Boolean firstTime;

    SharedPrefUtil sharedPref;

    private EditText Title;

    private Button selectFile;
    TextView Notification;
    String text;


    FirebaseStorage storage;//To upload files
    FirebaseDatabase database;// Used to store URLs of Uploaded files
    Uri videoUri;//urls meant for local storage
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String author;

    //database references

    ProgressDialog progressDialog;
    //Variables
    ImageView menuIcon;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_upload);
        sharedPref = new SharedPrefUtil(LectureUploadActivity.this);
        String dept = sharedPref.getString("userDept");
        final Query nm = FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("department").equalTo(dept);
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> subj = new ArrayList<String>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String subjName = areaSnapshot.child("name").getValue(String.class);
                        subj.add(subjName);
                    }

                    Spinner spinner = (Spinner) findViewById(R.id.spinnerVid);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(LectureUploadActivity.this, android.R.layout.simple_spinner_item, subj);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(LectureUploadActivity.this);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        author = sharedPref.getString("userName");
        navigationDrawer();


        //Storage in firebase database

        storage = FirebaseStorage.getInstance();//returns an obj of firebase storage
        database = FirebaseDatabase.getInstance();//returns an obj of Firebase Database

        selectFile = findViewById(R.id.choose_btn);
        Upload = findViewById(R.id.upload_video_btn);
        Title = findViewById(R.id.TitleVideo);
//        Cancel=findViewById(R.id.Cancel_btn);
        Title.addTextChangedListener(TitleEntry);
        Upload.setEnabled(false);
//        Cancel.setEnabled(false);

        Notification = findViewById(R.id.textViewVid);


        //Add file button listener
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(LectureUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectVideo();
                } else {
                    ActivityCompat.requestPermissions(LectureUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });


        //Onclick listener of Upload button
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoUri != null)//the user has selected file
                    uploadFile(videoUri);
                else
                    Toast.makeText(LectureUploadActivity.this, "Select a video", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
        // Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
    }

    //For the spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), " Nothing    ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Function to upload file to firebase database

    private void uploadFile(Uri videoUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.show();


        //final String fileName=System.currentTimeMillis()+"";
        StorageReference storageReference = storage.getReference().child("Videos/" + System.currentTimeMillis() + "." + getExt(videoUri));//returns root path
        storageReference.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri url = uri.getResult();

                        UploadVideoDetails details = new UploadVideoDetails(text, Title.getText().toString(), url.toString(), author);

                        database.getReference().child("Videos").child(database.getReference().push().getKey()).setValue(details);
                        progressDialog.dismiss();
                        Toast.makeText(LectureUploadActivity.this, "Video successfully uploaded", Toast.LENGTH_SHORT).show();
                        Notification.setText("Nothing Uploaded");
                        Title.setText("");
                        Upload.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LectureUploadActivity.this, "Video not successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //track the progress of the upload
                int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);

            }
        });

    }


    //Checking for grant of permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectVideo();
        } else {
            Toast.makeText(LectureUploadActivity.this, "Permission not provided", Toast.LENGTH_SHORT).show();
        }
    }

    //Video selecting function

    private void selectVideo() {

        //to offer user to select a file using file manager, using an intent

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent,PICK_VIDEO);

        startActivityForResult(intent, 90);

    }

    //overriding onActivityResult to check if user has successfully selected a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //resultcode to check if action is completed successfully
        //data to check if file is selected or not
        if (requestCode == 90 && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            Notification.setText("Video File selected: " + data.getData().getLastPathSegment());
        } else {
            Toast.makeText(LectureUploadActivity.this, "Please select a video", Toast.LENGTH_SHORT).show();
        }
    }

    //Text watcher to disable button
    private TextWatcher TitleEntry = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (s.toString().equals("")) {
                Upload.setEnabled(false);
                //Cancel.setEnabled(false);
            } else {
                Upload.setEnabled(true);
                //Cancel.setEnabled(true);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    //Navigation Drawer Functions
    private void navigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv_username = header.findViewById(R.id.nav_username);
        String n = sharedPref.getString("userName");
        if (currentUser.getDisplayName() != null) {
            if (n != null) {
                tv_username.setText(n);
            } else if (currentUser.getDisplayName() != "") {
                tv_username.setText(currentUser.getDisplayName());
            } else {
                tv_username.setText("Hello User!");
            }
            //String imgurl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
            if (currentUser.getPhotoUrl() != null) {
                String imgurl = currentUser.getPhotoUrl().toString();
                ImageView iv_userphoto = header.findViewById(R.id.userPhoto);
                Glide.with(this).load(imgurl).into(iv_userphoto);
            }
        }


        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        String teacher = sharedPref.getString("teacher");
        if (teacher.equals("1")) {
            navigationView.getMenu().setGroupVisible(R.id.pri, false);
        }

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                //startActivity(new Intent(this, LoginActivity.class));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                firstTime = true;
                editor.putBoolean("firstTime", firstTime);
                editor.apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                startActivity(new Intent(this, UpdateProfile.class));
                break;
            case R.id.nav_upload:
                startActivity(new Intent(this, UploadActivity.class));
                break;
            case R.id.your_notes:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.create_note:
                startActivity(new Intent(this, CreateNoteActivity.class));
                break;
            case R.id.nav_home:
                startActivity(new Intent(this, Home.class));
                break;
            case R.id.upload_lec_video:
                startActivity(new Intent(this, LectureUploadActivity.class));
                break;
            case R.id.videos_uploaded:
                startActivity(new Intent(this, CoursesRecyclerViewActivity.class));
                break;

            default:
                return true;
        }
        return true;

    }
}