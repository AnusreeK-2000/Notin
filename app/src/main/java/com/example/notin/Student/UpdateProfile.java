package com.example.notin.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.notin.Common.LoginActivity;
import com.example.notin.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    ImageView menuIcon;
    private FirebaseAuth mAuth;
    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        currentUser = mAuth.getInstance().getCurrentUser();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv_username = header.findViewById(R.id.nav_username);
        if(currentUser.getDisplayName() != "") {
            tv_username.setText(currentUser.getDisplayName());
        }else{
            tv_username.setText("Hello User!");
        }
        //String imgurl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        if(currentUser.getPhotoUrl() != null){
            String imgurl = currentUser.getPhotoUrl().toString();
            ImageView iv_userphoto = header.findViewById(R.id.userPhoto);
            Glide.with(this).load(imgurl).into(iv_userphoto);
        }
        updateProfile();
        navigationDrawer();

    }

    private void updateProfile() {

        if(currentUser.getPhotoUrl() != null){
            String imgurl = currentUser.getPhotoUrl().toString();
            ImageView iv_userphoto = findViewById(R.id.profile_image);
            Glide.with(this).load(imgurl).into(iv_userphoto);
        }
        TextView tv_username = findViewById(R.id.full_name);
        TextInputEditText user = findViewById(R.id.inputname);
        if(currentUser.getDisplayName() != "") {
            tv_username.setText(currentUser.getDisplayName());
            user.setText(currentUser.getDisplayName());
        }else{
            tv_username.setText("Hello User!");
            user.setText("");
        }
        TextView email = findViewById(R.id.email);
        TextView emailedit = findViewById(R.id.editEmail);
        if(currentUser.getEmail() != "") {
            email.setText(currentUser.getEmail());
            emailedit.setText(currentUser.getEmail());
        }else{
            email.setText("");
            emailedit.setText("");
        }

    }

    //Navigation Drawer Functions
    private void navigationDrawer() {

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
                startActivity(new Intent(this, LoginActivity.class));
                break;
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

            default:
                return true;
        }
        return true;

    }
}