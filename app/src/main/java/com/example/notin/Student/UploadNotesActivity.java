package com.example.notin.Student;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.adapters.MyListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class UploadNotesActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION=1;
    public static ArrayList<File> mfiles=new ArrayList<>();
    File folder;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_display);

        //Course Name
        String name = getIntent().getExtras().get("subject").toString();

        TextView courseName = (TextView) findViewById (R.id.textMyNotes);
        courseName.setText(name);


        //back button
        ImageView imageBack = findViewById(R.id.home_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UploadNotesActivity.this,Home.class));
            }
        });

        //to upload notes
        ImageView uploading=findViewById(R.id.uploading);
        uploading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(UploadNotesActivity.this,UploadActivity.class));
            }
        });


/*
        myListData[] myListData=new myListData[]{
                new myListData("Unix Unit 1 Notes", "Vikranth B.M"),
        new myListData("Computer Networks Unit 3 Notes", "Lohit J.J"),
        new myListData("Linear Algebra chap 2 Notes", "Shikha"),
                new myListData("Skip List,B trees,AVl trees", "Namratha"),
                new myListData("Requirements", "Anusree"),
                new myListData("Software Project Planning ", "Pradeep"),
                new myListData("Notes 5", "Arpana"),
                new myListData("AI Chapter 4", "Nanma"),
                new myListData("pdf4", "Micheal"),
                new myListData("Dynamic Programming notes 3", "PK"),
                new myListData("Data structures cie2", "Jimmy"),



        };

 */
        //permission method..
        permission();


        recyclerView = (RecyclerView) findViewById(R.id.uploadRecyclerView);
        uploadNotesView(name);
        /*
        MyListAdapter adapter = new MyListAdapter(this,myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);

         */
    }

    private void uploadNotesView(String name) {
        final ArrayList<UploadPDFDetails> notesHelper = new ArrayList<UploadPDFDetails>();

        final Query nm= FirebaseDatabase.getInstance().getReference().child("Notes")
                .orderByChild("subject")
                .equalTo(name);

        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        UploadPDFDetails l=postSnapshot.getValue(UploadPDFDetails.class);
                        notesHelper.add(l);
                    }
                    adapter = new MyListAdapter(UploadNotesActivity.this,notesHelper);
                    recyclerView.setLayoutManager(new LinearLayoutManager(UploadNotesActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapter);
                    //System.out.println(notesHelper);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setHasFixedSize(true);

    }


    private void permission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(com.example.notin.Student.UploadNotesActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this,"Please Grant Permission",Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(com.example.notin.Student.UploadNotesActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
        }
        else{
            Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show();
            initViews();
        }
   }

    private void initViews() {
       folder=new File(Environment.getExternalStorageDirectory().getAbsolutePath());
       mfiles=getPdfNames(folder);

    }

    private ArrayList<File> getPdfNames(File folder) {
        ArrayList<File> arrayList=new ArrayList<>();
        File [] files= folder.listFiles();
        if(files!=null){
            for(File singleFile: files){
                if(singleFile.isDirectory()&& !singleFile.isHidden()){
                    arrayList.addAll(getPdfNames(singleFile));
                }
                else{
                    if(singleFile.getName().endsWith(".pdf")){
                        arrayList.add(singleFile);
                    }
                }
            }
            return arrayList;
        }
        return arrayList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show();
                initViews();
            }
            else{
                Toast.makeText(this,"Please Grant Permission",Toast.LENGTH_SHORT).show();
            }
        }
    }
}