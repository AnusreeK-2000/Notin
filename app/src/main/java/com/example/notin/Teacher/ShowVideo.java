package com.example.notin.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowVideo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public RecyclerView mRecyclerView2;
    FirebaseDatabase mFirebaseDatabase2;
    Query mref2;
    private FirebaseAuth mAuth;
    Boolean firstTime;
    SharedPrefUtil sharedPref;
    FirebaseUser currentUser;
    String name,url;

    ImageView menuIcon;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        sharedPref = new SharedPrefUtil(ShowVideo.this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String name = getIntent().getExtras().get("name").toString();

        mRecyclerView2 = findViewById(R.id.recyclerview_ShowVideo);
        mRecyclerView2.setHasFixedSize(true);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mref2 = mFirebaseDatabase2.getReference().child("Videos").orderByChild("subject").equalTo(name);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();


    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UploadVideoDetails> options =
                new FirebaseRecyclerOptions.Builder<UploadVideoDetails>()
                        .setQuery(mref2,UploadVideoDetails.class)
                        .build();

        FirebaseRecyclerAdapter<UploadVideoDetails, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UploadVideoDetails, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UploadVideoDetails model) {

                        holder.setDetails2(getApplication(),model.getName(),model.getUrl());
                        holder.setOnClicklistener(new ViewHolder.Clicklistener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                name = getItem(position).getName();
                                url = getItem(position).getUrl();
                                Intent intent = new Intent(ShowVideo.this,Fullscreen.class);
                                intent.putExtra("nam",name);
                                intent.putExtra("ur",url);
                                startActivity(intent);


                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                name = getItem(position).getName();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row,parent,false);


                        return new ViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        mRecyclerView2.setAdapter(firebaseRecyclerAdapter);

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
            case R.id.videos_uploaded:
                startActivity(new Intent(this, CoursesRecyclerViewActivity.class));
                break;

            default:
                return true;
        }
        return true;

    }
}