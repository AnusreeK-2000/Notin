package com.example.notin.Student;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.notin.Common.LoginActivity;
import com.example.notin.R;
import com.example.notin.Teacher.LectureUploadActivity;
import com.example.notin.Utils.SharedPrefUtil;
import com.example.notin.adapters.RecentNotesAdapter;
import com.example.notin.entities.RecentNotes;
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

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private Button Confirm;

    Boolean firstTime;

    SharedPrefUtil sharedPref;

    private EditText Title;

    private ImageView info;

    private Button selectFile;
    TextView Notification;
    String text;


    FirebaseStorage storage;//To upload files
    FirebaseDatabase database;// Used to store URLs of Uploaded files
    Uri pdfUri;//urls meant for local storage
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
        setContentView(R.layout.activity_upload);
        PopupWindow popUp = new PopupWindow(this);
        //To add the drop down menu of subjects
        sharedPref = new SharedPrefUtil(UploadActivity.this);
        String dept = sharedPref.getString("userDept");
        final Query nm= FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("department").equalTo(dept);
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> subj = new ArrayList<String>();
                if (dataSnapshot.exists()){
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String subjName = areaSnapshot.child("name").getValue(String.class);
                        subj.add(subjName);
                    }

                    Spinner spinner = (Spinner) findViewById(R.id.spinner1);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.simple_spinner_item, subj);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(UploadActivity.this);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Spinner spinner = findViewById(R.id.spinner1);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Subjects,android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        //sharedPref = new SharedPrefUtil(UploadActivity.this);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        author = sharedPref.getString("userName");
        navigationDrawer();


        //Storage in firebase database

        storage=FirebaseStorage.getInstance();//returns an obj of firebase storage
        database=FirebaseDatabase.getInstance();//returns an obj of Firebase Database

        selectFile=findViewById(R.id.upload_btn);

        


//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        View header = navigationView.getHeaderView(0);
//        TextView tv_username = header.findViewById(R.id.nav_username);
//        if(currentUser.getDisplayName() != "") {
//            tv_username.setText(currentUser.getDisplayName());
//        }else{
//            tv_username.setText("Hello User!");
//        }
//        //String imgurl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
//        if(currentUser.getPhotoUrl() != null){
//            String imgurl = currentUser.getPhotoUrl().toString();
//            ImageView iv_userphoto = header.findViewById(R.id.userPhoto);
//            Glide.with(this).load(imgurl).into(iv_userphoto);
//        }

        /*FOR NOW ACCESSING CREATE VIA CLICKING CANCEL BUTTON
        Button btn = (Button)findViewById(R.id.Cancel_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Create.class));
            }
        });
*/
        //To disable confirm and cancel
        Confirm=findViewById(R.id.confirm_btn);
        Title=findViewById(R.id.TitleText);
//        Cancel=findViewById(R.id.Cancel_btn);
        Title.addTextChangedListener(TitleEntry);
        Confirm.setEnabled(false);
//        Cancel.setEnabled(false);

        selectFile=findViewById(R.id.upload_btn);
        Notification=findViewById(R.id.textView);

        //Add file button listener
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPdf();
                }
                else{
                    ActivityCompat.requestPermissions(UploadActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        //Onclick listener of Upload button
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri!=null)//the user has selected file
                    uploadFile(pdfUri);
                else
                    Toast.makeText(UploadActivity.this,"Select a file",Toast.LENGTH_SHORT).show();
            }
        });
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

    private void uploadFile(Uri pdfUri){
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.show();


        //final String fileName=System.currentTimeMillis()+"";
        StorageReference storageReference=storage.getReference().child("Uploads/"+System.currentTimeMillis()+".pdf");//returns root path
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri>uri=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url =uri.getResult();

                        UploadPDFDetails details=new UploadPDFDetails(Title.getText().toString(),url.toString(),text,author);

                        database.getReference().child("Notes").child(database.getReference().push().getKey()).setValue(details);
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this,"File successfully uploaded",Toast.LENGTH_SHORT).show();
                        Notification.setText("Nothing Uploaded");
                        Title.setText("");
                        Confirm.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this,"File not successfully uploaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //track the progress of the upload
                int currentProgress=(int)(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);

            }
        });

    }



    //Checking for grant of permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }
        else{
            Toast.makeText(UploadActivity.this,"Permission not provided",Toast.LENGTH_SHORT).show();
        }
    }

    //Pdf selecting function

    private void selectPdf(){

        //to offer user to select a file using file manager, using an intent

        Intent intent=new Intent();
        intent.setType("application/pdf");//setting type as pdf
        intent.setAction(Intent.ACTION_GET_CONTENT);//fetch files

        startActivityForResult(intent,90);

    }

    //overriding onActivityResult to check if user has successfully selected a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //resultcode to check if action is completed successfully
        //data to check if file is selected or not
        if(requestCode==90 && resultCode==RESULT_OK && data!=null){
            pdfUri=data.getData();
            Notification.setText("File selected: "+data.getData().getLastPathSegment());
        }
        else{
            Toast.makeText(UploadActivity.this,"Please select a file",Toast.LENGTH_SHORT).show();
        }
    }

    //Text watcher to disable button
    private TextWatcher TitleEntry = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (s.toString().equals("")) {
                Confirm.setEnabled(false);
                //Cancel.setEnabled(false);
            } else {
                Confirm.setEnabled(true);
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
        if(currentUser.getDisplayName() != null){
            if(n != null){
                tv_username.setText(n);
            }
            else if (currentUser.getDisplayName() != "") {
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
        if(teacher.equals("1")){
            navigationView.getMenu().setGroupVisible(R.id.pri, false);
        }
        else{
            navigationView.getMenu().setGroupVisible(R.id.priTeach, false);
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
                startActivity(intent);break;
            case R.id.nav_profile:
                startActivity(new Intent(this, UpdateProfile.class));
                break;
            case R.id.nav_upload:
                startActivity(new Intent(this,UploadActivity.class));
                break;
            case R.id.your_notes:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.create_note:
                startActivity(new Intent(this,CreateNoteActivity.class));
                break;
            case R.id.nav_home:
                startActivity(new Intent(this, Home.class));
                break;
            case R.id.upload_lec_video:
                startActivity(new Intent(this, LectureUploadActivity.class));
                break;

            default:
                return true;
        }
        return true;

    }

    /*public void Create() {
        Intent intent;
        intent = new Intent(this, Create.class);
        startActivity(intent);
    }*/


}

