package com.example.notin.Teacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notin.Common.LoginActivity;
import com.example.notin.R;
import com.example.notin.Student.CreateNoteActivity;
import com.example.notin.Student.Home;
import com.example.notin.Student.MainActivity;
import com.example.notin.Student.UpdateProfile;
import com.example.notin.Student.UploadActivity;
import com.example.notin.Utils.SharedPrefUtil;
import com.example.notin.adapters.VideoCoursesAdapter;
import com.example.notin.entities.Courses;
import com.example.notin.entities.UploadVideoDetails;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CoursesRecyclerViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    Boolean firstTime;

    //Variables
    ImageView menuIcon;
    ImageView leadToUpload;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    RecyclerView recentNotesRecycler, coursesRecycler;
    //    private FirebaseFirestore db;
    //RecyclerView.Adapter adapter;
    VideoCoursesAdapter adapter;
    final ArrayList<Courses> coursesHelperClasses = new ArrayList<Courses>();

    SharedPrefUtil sharedPref;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_courses_recycler_view);

        sharedPref = new SharedPrefUtil(CoursesRecyclerViewActivity.this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();

        //Hooks
        coursesRecycler = findViewById(R.id.courses_view);
        leadToUpload = findViewById(R.id.lead_to_upload);
        leadToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedPref.getString("teacher").equals("1")){
                        startActivity(new Intent(CoursesRecyclerViewActivity.this, LectureUploadActivity.class));
                }else {
                    startActivity(new Intent(CoursesRecyclerViewActivity.this, UploadActivity.class));
                }
            }
        });

        //Functions will be executed automatically when this activity will be created


        coursesRecycler();
        //for searching courses
        EditText dis=findViewById(R.id.video_courses);
        dis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (coursesHelperClasses.size() != 0) {
                    adapter.searchNotes(s.toString());
                }

            }
        });
    }

    private void coursesRecycler(){
        String dept = sharedPref.getString("userDept");
//        coursesHelperClasses.add(new Courses("Advanced Data Structures"));
//        coursesHelperClasses.add(new Courses("Computer Networks"));
//        coursesHelperClasses.add(new Courses("UNIX Shell Programming"));
//        coursesHelperClasses.add(new Courses("Advanced Algorithms"));
//        coursesHelperClasses.add(new Courses("Artificial Intelligence"));
//        coursesHelperClasses.add(new Courses("Software Engineering"));
//        coursesHelperClasses.add(new Courses("Software Project Management and Finance"));

        final Query nm= FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("department").equalTo(dept);
        nm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Courses l=npsnapshot.getValue(Courses.class);
                        coursesHelperClasses.add(l);
                    }
//                                                      adapter=new MyAdapter(listData);
//                                                      rv.setAdapter(adapter);
                    adapter = new VideoCoursesAdapter(coursesHelperClasses);
                    coursesRecycler.setLayoutManager(new LinearLayoutManager(CoursesRecyclerViewActivity.this, LinearLayoutManager.VERTICAL, false));
                    coursesRecycler.setAdapter(adapter);
                    System.out.println(coursesHelperClasses);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Query query = db.collection("Notes");

//        FirestoreRecyclerOptions<RecentNotes> recentNotesHelperClasses = new FirestoreRecyclerOptions.Builder<RecentNotes>()
//                .setQuery(query, RecentNotes.class)
//                .build();
        coursesRecycler.setHasFixedSize(true);

    }



    //Navigation Drawer Functions
    private void navigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv_username = header.findViewById(R.id.nav_username);
        String n = sharedPref.getString("userName");
        if(currentUser != null){
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
        String teacher = sharedPref.getString("teacher");
        if(teacher.equals("1")){
            navigationView.getMenu().setGroupVisible(R.id.pri, false);
        }
        else{
            navigationView.getMenu().setGroupVisible(R.id.priTeach, false);
        }

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

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

            default:
                return true;
        }
        return true;

    }

}